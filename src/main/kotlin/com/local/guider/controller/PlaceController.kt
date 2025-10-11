package com.local.guider.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.local.guider.entities.Place
import com.local.guider.models.BaseResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.network_utils.FileUtils
import com.local.guider.services.PlaceService
import com.local.guider.utils.TimeUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class PlaceController(
    private val placeService: PlaceService
) {

    @PostMapping(Endpoints.ADD_PLACE)
    fun addPlace(
        @RequestParam("featuredImage") featuredImage: MultipartFile?,
        @RequestParam("placeName") placeName: String?,
        @RequestParam("description") description: String?,
        @RequestParam("state") state: String? = null,
        @RequestParam("city") city: String? = null,
        @RequestParam("topPlace") topPlace: Boolean? = null,
        @RequestParam("address") address: String? = null,
        @RequestParam("mapUrl") mapUrl: String? = null,
        @RequestParam("latitude") latitude: Double? = null,
        @RequestParam("longitude") longitude: Double? = null,
        @RequestParam("openAllDay") openAllDay: Boolean? = null,
        @RequestParam("timing") timing: String? = null
    ): BaseResponse<Place> {
        if (placeName == null) {
            return BaseResponse<Place>().failed(
                message = "Place name is required."
            )
        }
        if (placeService.existByPlaceName(placeName)) {
            return BaseResponse<Place>().failed(
                message = "Place is already exists."
            )
        }
        if (description == null) {
            return BaseResponse<Place>().failed(
                message = "Place description is required."
            )
        }
        if (featuredImage == null) {
            return BaseResponse<Place>().failed(
                message = "Featured image is required."
            )
        }
        if (state == null) {
            return BaseResponse<Place>().failed(
                message = "State is required."
            )
        }

        if (latitude == null) {
            return BaseResponse<Place>().failed(
                message = "Latitude is required."
            )
        }
        if (longitude == null) {
            return BaseResponse<Place>().failed(
                message = "Longitude is required."
            )
        }
        val newPlace = Place()
        if (openAllDay == null && timing == null) {
            newPlace.openAllDay = true
        }
        if (timing != null
        ) {
            try {
                val timesList =
                    Gson().fromJson<ArrayList<String>>(timing, object : TypeToken<ArrayList<String>>() {}.type)
                timesList.forEach {
                    val parts = it.split(',')
                    val day = parts.first()
                    val openTime = parts[1]
                    val closeTime = parts[2]
                }
            } catch (e: Exception) {
                return BaseResponse<Place>().failed(
                    message = "Time format is incorrect."
                )
            }
            newPlace.timing = timing
        }
        val savedImage = FileUtils.saveImage("Place_$placeName", featuredImage)
            ?: return BaseResponse<Place>().failed(
                message = "Unable to save featured image."
            )
        newPlace.placeName = placeName
        newPlace.description = description
        newPlace.featuredImage = savedImage
        newPlace.latitude = latitude
        newPlace.longitude = longitude
        newPlace.state = state
        newPlace.city = city
        newPlace.rating = 0.0
        newPlace.fullAddress = address
        newPlace.views = 0
        newPlace.isTop = topPlace ?: false
        newPlace.mapUrl = mapUrl
        newPlace.createdOn = TimeUtils.getCurrentDateTime()
        newPlace.lastUpdate = TimeUtils.getCurrentDateTime()
        placeService.save(newPlace)
        return BaseResponse<Place>().success(
            message = "New place added successfully.",
            data = newPlace
        )
    }

    @PostMapping(Endpoints.EDIT_PLACE)
    fun editPlace(
        @RequestParam("placeId") placeId: Long?,
        @RequestParam("featuredImage") featuredImage: MultipartFile?,
        @RequestParam("placeName") placeName: String?,
        @RequestParam("description") description: String?,
        @RequestParam("state") state: String? = null,
        @RequestParam("city") city: String? = null,
        @RequestParam("address") address: String? = null,
        @RequestParam("topPlace") topPlace: Boolean? = null,
        @RequestParam("mapUrl") mapUrl: String? = null,
        @RequestParam("latitude") latitude: Double? = null,
        @RequestParam("longitude") longitude: Double? = null,
        @RequestParam("openAllDay") openAllDay: Boolean? = null,
        @RequestParam("timing") timing: String? = null
    ): BaseResponse<Place> {
        if (placeId == null) {
            return BaseResponse<Place>().failed(
                message = "Place Id is required."
            )
        }
        val place = placeService.findById(placeId)
            ?: return BaseResponse<Place>().failed(
                message = "Place not found."
            )
        if (placeName.isNullOrEmpty().not()) place.placeName = placeName
        if (description.isNullOrEmpty().not()) place.description = description
        if (state.isNullOrEmpty().not()) place.state = state
        if (latitude != null) place.latitude = latitude
        if (longitude != null) place.longitude = longitude
        if (openAllDay != null) place.openAllDay = openAllDay
        if (address.isNullOrEmpty().not()) place.fullAddress = address
        if (mapUrl.isNullOrEmpty().not()) place.mapUrl = mapUrl
        if (city.isNullOrEmpty().not()) place.city = city
        if (topPlace != null) place.isTop = topPlace

        if (timing != null
        ) {
            try {
                val timesList =
                    Gson().fromJson<ArrayList<String>>(timing, object : TypeToken<ArrayList<String>>() {}.type)
                timesList.forEach {
                    val parts = it.split(',')
                    val day = parts.first()
                    val openTime = parts[1]
                    val closeTime = parts[2]
                }
            } catch (e: Exception) {
                return BaseResponse<Place>().failed(
                    message = "Time format is incorrect."
                )
            }
            place.timing = timing
        }
        if (featuredImage != null) {
            val savedImage = FileUtils.saveImage("Place_$placeName", featuredImage)
                ?: return BaseResponse<Place>().failed(
                    message = "Unable to save featured image."
                )
            place.featuredImage = savedImage
        }
        place.lastUpdate = TimeUtils.getCurrentDateTime()
        placeService.save(place)
        return BaseResponse<Place>().success(
            message = "Place updated successfully.",
            data = place
        )
    }

    @PostMapping(Endpoints.GET_PLACES)
    fun getPlaces(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int,
        @RequestParam searchText: String? = null,
        @RequestParam topPlaces: Boolean? = null,
        @RequestParam latitude: Double?,
        @RequestParam longitude: Double?,
    ): BaseResponse<List<Place>?> {
        var mPerPage = perPage
        if (topPlaces == true) {
            mPerPage = 20
        }
        val pageable = PageRequest.of(page - 1, mPerPage)
        var places: Page<Place>? = null
        places = if(topPlaces == true) {
            placeService.findTopPlace(pageable)
        } else if (latitude != null && longitude != null) {
            placeService.findNearBy(
                latitude = latitude,
                longitude = longitude,
                searchText = searchText ?: "",
                pageable
            )
        } else {
            placeService.findBySearchText(
                searchText ?: "",
                pageable
            )
        }
        return BaseResponse<List<Place>?>().success(
            message = "Places fetched successfully",
            data = places?.toList() ?: emptyList(),
            page = page,
            mTotalPage = places?.totalPages ?: 0
        )
    }

    @PostMapping(Endpoints.GET_PLACES_BY_IDS)
    fun getPlacesByIds(
        @RequestParam ids: String? = null,
    ): BaseResponse<List<Place>?> {
        val places = ArrayList<Place>()
        ids?.split(",")?.forEach {
            val place = placeService.findById(it.toLong())
            if (place != null) {
                places.add(place)
            }
        }
        return BaseResponse<List<Place>?>().success(
            message = "Places fetched successfully",
            data = places,
        )
    }

    @PostMapping(Endpoints.DELETE_PLACE)
    fun deletePlace(
        @RequestParam("placeId") placeId: Long?,
    ): BaseResponse<String?> {
        if (placeId == null) {
            return BaseResponse<String?>().failed(
                message = "Place id is required."
            )
        }
        if (!placeService.existById(placeId)) {
            return BaseResponse<String?>().failed(
                message = "Place not found."
            )
        }
        placeService.deleteById(placeId)
        return BaseResponse<String?>().success(
            message = "Place deleted successfully",
            data = ""
        )
    }

    @PostMapping(Endpoints.ADD_VIEW)
    fun addView(
        @RequestParam("placeId") placeId: Long?,
    ): BaseResponse<String?> {
        if (placeId == null) {
            return BaseResponse<String?>().failed(
                message = "Place id is required."
            )
        }
        val place = placeService.findById(placeId)
            ?: return BaseResponse<String?>().failed(
                message = "Place not found."
            )
        place.views = (place.views ?: 0) + 1
        placeService.save(place)
        return BaseResponse<String?>().success(
            message = "Place view added successfully",
            data = ""
        )
    }

}