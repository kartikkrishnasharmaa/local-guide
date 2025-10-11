package com.local.guider.controller

import com.local.guider.entities.Service
import com.local.guider.models.BaseResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.network_utils.FileUtils
import com.local.guider.services.*
import com.local.guider.utils.TimeUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import kotlin.jvm.optionals.getOrElse

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class ServiceController(
    private val userService: UserService,
    private val photographerService: PhotographerService,
    private val guiderService: GuiderService,
    private val servicesService: ServicesService
) {

    @PostMapping(Endpoints.CREATE_SERVICE)
    fun createService(
        @RequestParam("photographerId") photographerId: Long?,
        @RequestParam("guiderId") guiderId: Long?,
        @RequestParam("title") title: String?,
        @RequestParam("description") description: String?,
        @RequestParam("servicePrice") servicePrice: Double?,
        @RequestParam("image") image: MultipartFile?,
    ): BaseResponse<Service?> {
        if (guiderId == null && photographerId == null) {
            return BaseResponse<Service?>().failed("Please provide guiderId or photographerId")
        }
        if (title == null) {
            return BaseResponse<Service?>().failed("Title is required")
        }
        if (servicePrice == null) {
            return BaseResponse<Service?>().failed("Price is required")
        }

        val newService = Service()
        newService.title = title
        newService.description = description
        newService.servicePrice = servicePrice
        newService.guiderId = guiderId
        newService.photographerId = photographerId

        if (image != null) {
            val savedImage = FileUtils.saveImage("Service_$title", image)
            newService.image = savedImage
        }

        newService.createdOn = TimeUtils.getCurrentDateTime()
        newService.lastUpdate = TimeUtils.getCurrentDateTime()
        servicesService.save(newService)
        return BaseResponse<Service?>().success("Service saved successfully", data = newService)
    }

    @PostMapping(Endpoints.UPDATE_SERVICE)
    fun updateService(
        @RequestParam("photographerId") photographerId: Long?,
        @RequestParam("guiderId") guiderId: Long?,
        @RequestParam("serviceId") serviceId: Long?,
        @RequestParam("title") title: String?,
        @RequestParam("description") description: String?,
        @RequestParam("servicePrice") servicePrice: Double?,
        @RequestParam("image") image: MultipartFile?
    ): BaseResponse<Service?> {
        if (serviceId == null) return BaseResponse<Service?>().failed(message = "Service id is required.")
        val service = servicesService.getById(serviceId).getOrElse {
            return BaseResponse<Service?>().failed("Service not found")
        }
        if (title != null) {
            service.title = title
        }

        if (servicePrice != null) {
            service.servicePrice = servicePrice
        }

        if (description != null) {
            service.description = description
        }

        if (image != null) {
            val savedImage = FileUtils.saveImage("Service_$title ${TimeUtils.getCurrentDateTime()}", image)
            service.image = savedImage
        }
        service.lastUpdate = TimeUtils.getCurrentDateTime()
        servicesService.save(service)
        return BaseResponse<Service?>().success("Service updated successfully", data = service)
    }

    @PostMapping(Endpoints.DELETE_SERVICE)
    fun deleteService(
        @RequestParam("serviceId") serviceId: Long
    ): BaseResponse<Any?> {
        servicesService.deleteById(serviceId)
        return BaseResponse<Any?>().success("Service deleted successfully", data = null)
    }

    @PostMapping(Endpoints.GET_SERVICES)
    fun getServices(
        @RequestParam("photographerId") photographerId: Long? = null,
        @RequestParam("guiderId") guiderId: Long? = null,
    ): BaseResponse<Any?> {
        if (photographerId == null && guiderId == null) {
            return BaseResponse<Any?>().failed("Please provide guiderId or photographerId")
        }
        val services = if (photographerId != null) {
            servicesService.getByPhotographerId(photographerId) ?: emptyList()
        } else if (guiderId != null) {
            servicesService.getByGuiderId(guiderId) ?: emptyList()
        } else {
            emptyList()
        }
        return BaseResponse<Any?>().success("Services fetched successfully", data = services)
    }

}