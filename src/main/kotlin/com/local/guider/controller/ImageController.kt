package com.local.guider.controller

import com.local.guider.entities.Image
import com.local.guider.models.BaseResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.network_utils.FileUtils
import com.local.guider.services.*
import com.local.guider.utils.TimeUtils
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.FileInputStream

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class ImageController(
    private val placeService: PlaceService,
    private val photographerService: PhotographerService,
    private val guiderService: GuiderService,
    private val imageService: ImageService

) {

    @PostMapping(Endpoints.ADD_IMAGE)
    fun addImage(
        @RequestParam("image") image: MultipartFile?,
        @RequestParam("title") title: String?,
        @RequestParam("description") description: String?,
        @RequestParam("photographerId") photographerId: Long? = null,
        @RequestParam("guiderId") guiderId: Long? = null,
        @RequestParam("placeId") placeId: Long? = null
    ): BaseResponse<String?> {
        if (image == null || image.isEmpty) return BaseResponse<String?>().failed(
            message = "Invalid file"
        )
        if (photographerId == null && guiderId == null && placeId == null) return BaseResponse<String?>().failed(
            message = "No Id specified"
        )
        var savedImage: String? = null
        val newImage = Image()
        if (photographerId != null) {
            if (!photographerService.existsById(photographerId)) return BaseResponse<String?>().failed(
                message = "Invalid photographer id!"
            )
            try {
                savedImage = FileUtils.saveImage("Photographer_$photographerId", image)
                newImage.photographerId = photographerId
                newImage.image = savedImage
            } catch (e: Exception) {
                return BaseResponse<String?>().failed(
                    message = e.message
                )
            }

        } else if (guiderId != null) {
            if (!guiderService.existsById(guiderId)) return BaseResponse<String?>().failed(
                message = "Invalid guider id!"
            )
            savedImage = FileUtils.saveImage("Guider_$guiderId", image)
            newImage.guiderId = guiderId
            newImage.image = savedImage
        } else if (placeId != null) {
            if (!placeService.existById(placeId)) return BaseResponse<String?>().failed(
                message = "Invalid place id!"
            )
            savedImage = FileUtils.saveImage("Place_$placeId", image)
            newImage.placeId = placeId
            newImage.image = savedImage
        }
        newImage.title = title
        newImage.description = description
        newImage.createdOn = TimeUtils.getCurrentDateTime()
        newImage.lastUpdate = TimeUtils.getCurrentDateTime()
        imageService.save(newImage)
        return BaseResponse<String?>().success(
            message = "Image saved successfully",
            data = savedImage
        )
    }

    @PostMapping(Endpoints.ALL_IMAGES)
    fun getImages(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int
    ): BaseResponse<List<Image>?> {
        val pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "lastUpdate"))
        val images = imageService.findByPage(pageable)
            ?: return BaseResponse<List<Image>?>().failed(
                message = "No Image found"
            )
        return BaseResponse<List<Image>?>().success(
            message = "Images fetched successfully.",
            data = images.toList(),
            mTotalPage = images.totalPages,
            page = page
        )
    }

    @PostMapping(Endpoints.ALL_IMAGES_BY_ID)
    fun getImagesById(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int,
        @RequestParam("photographerId") photographerId: Long? = null,
        @RequestParam("guiderId") guiderId: Long? = null,
        @RequestParam("placeId") placeId: Long? = null
    ): BaseResponse<List<Image>?> {
        if (photographerId == null && guiderId == null && placeId == null) return BaseResponse<List<Image>?>().failed(
            message = "No Id specified"
        )
        val pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "lastUpdate"))
        var images: Page<Image>? = null
        if (photographerId != null) {
            if (!photographerService.existsById(photographerId)) return BaseResponse<List<Image>?>().failed(
                message = "Invalid photographer id!"
            )
            images = imageService.findByPhotographerId(photographerId, pageable)
                ?: return BaseResponse<List<Image>?>().failed(
                    message = "No Image found"
                )
        } else if (guiderId != null) {
            if (!guiderService.existsById(guiderId)) return BaseResponse<List<Image>?>().failed(
                message = "Invalid guider id!"
            )
            images = imageService.findByGuiderId(guiderId, pageable)
                ?: return BaseResponse<List<Image>?>().failed(
                    message = "No Image found"
                )
        } else if (placeId != null) {
            if (!placeService.existById(placeId)) return BaseResponse<List<Image>?>().failed(
                message = "Invalid place id!"
            )
            images = imageService.findByPlaceId(placeId, pageable)
                ?: return BaseResponse<List<Image>?>().failed(
                    message = "No Image found"
                )
        }
        return BaseResponse<List<Image>?>().success(
            message = "Image fetched successfully",
            data = images?.toList() ?: emptyList()
        )
    }

    @PostMapping(Endpoints.DELETE_IMAGE)
    fun deleteImageById(
        @RequestParam("imageId") imageId: Long?,
    ): BaseResponse<String?> {
        if (imageId == null) return BaseResponse<String?>().failed("Image id required")
        imageService.deleteById(imageId)
        return BaseResponse<String?>().success(
            data = "Image deleted successfully",
            message = "Image deleted successfully"
        )
    }

    @GetMapping(Endpoints.DOWNLOAD_IMAGE)
    fun downloadImage(
        @PathVariable("path") path: String
    ): ResponseEntity<Resource?> {

        val imageFile = FileUtils.getImage("${FileUtils.FILES_DIRECTORY}/files/$path") ?: return ResponseEntity.notFound().build()
        val resource = InputStreamResource(FileInputStream(imageFile))

        val header = HttpHeaders()
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=img.jpg")
        header.add("Cache-Control", "no-cache, no-store, must-revalidate")
        header.add("Pragma", "no-cache")
        header.add("Expires", "0")

        return ResponseEntity.ok()
            .headers(header)
            .contentLength(imageFile.length())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body<Resource>(resource)
    }


}