package com.local.guider.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.local.guider.entities.Guider
import com.local.guider.entities.Notification
import com.local.guider.entities.Photographer
import com.local.guider.entities.Service
import com.local.guider.enumuration.AppointmentStatus
import com.local.guider.enumuration.ApprovalStatus
import com.local.guider.models.BaseResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.network_utils.FileUtils
import com.local.guider.services.*
import com.local.guider.utils.TimeUtils
import com.local.guider.utils.Utils.allowedGovtIdTypes
import com.local.guider.utils.mkFirstUppercase
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class PhotographerController(
    private val placeService: PlaceService,
    private val userService: UserService,
    private val servicesService: ServicesService,
    private val photographerService: PhotographerService,
    private val timeslotService: TimeslotService,
    private val notificationService: NotificationService,
) {

    @PostMapping(Endpoints.REQUEST_FOR_PHOTOGRAPHER)
    fun requestForPhotographer(
        request: MultipartHttpServletRequest,
    ): BaseResponse<Photographer?> {

        val userId = request.getParameter("userId")?.toLongOrNull()
        val firmName = request.getParameter("firmName")
        val featuredImage = request.getFile("featuredImage")
        val idProofFront = request.getFile("idProofFront")
        val idProofBack = request.getFile("idProofBack")
        val photograph = request.getFile("photograph")
        val idProofType = request.getParameter("idProofType")
        val description = request.getParameter("description")
        val phone = request.getParameter("phone")
        val email = request.getParameter("email")
        val placeId = request.getParameter("placeId")?.toLongOrNull()
        val places = request.getParameter("places")
        val address = request.getParameter("address")

        if (userId == null) {
            return BaseResponse<Photographer?>().failed(
                message = "User id is required."
            )
        }
        if (firmName == null) {
            return BaseResponse<Photographer?>().failed(
                message = "Name is required."
            )
        }
        if (idProofFront == null) {
            return BaseResponse<Photographer?>().failed(
                message = "Id proof front is required."
            )
        }
        if (idProofBack == null) {
            return BaseResponse<Photographer?>().failed(
                message = "Id proof back is required."
            )
        }
        if (photograph == null) {
            return BaseResponse<Photographer?>().failed(
                message = "Photograph is required."
            )
        }
        if (address == null) {
            return BaseResponse<Photographer?>().failed(
                message = "Address is required."
            )
        }
        if (phone == null) {
            return BaseResponse<Photographer?>().failed(
                message = "Phone is required."
            )
        }
        if (placeId == null) {
            return BaseResponse<Photographer?>().failed(
                message = "Place is required."
            )
        }
        if (places == null) {
            return BaseResponse<Photographer?>().failed(
                message = "Place is required."
            )
        }
        if (idProofType !in allowedGovtIdTypes) {
            return BaseResponse<Photographer?>().failed(
                message = "This id proof is not allowed."
            )
        }
        if (photographerService.existsByUserId(userId)) {
            return BaseResponse<Photographer?>().failed(
                message = "Request is already exists."
            )
        }
        val place = placeService.findById(placeId)
            ?: return BaseResponse<Photographer?>().failed(
                message = "Invalid place id."
            )

        val services = ArrayList<Service>()

        for (paramName in request.parameterMap.keys) {
            if (paramName.startsWith("services[")) {
                val serviceIndex = paramName.substringAfter("[").substringBefore("]").toIntOrNull()

                if (serviceIndex != null && serviceIndex >= services.size) {
                    val title = request.getParameter("services[$serviceIndex][title]")
                    val mDescription = request.getParameter("services[$serviceIndex][description]")
                    val servicePrice = request.getParameter("services[$serviceIndex][servicePrice]")
                    val image = request.getFile("services[$serviceIndex][image]")

                    if (title.isNullOrEmpty()) {
                        return BaseResponse<Photographer?>().failed(
                            message = "Service[$serviceIndex] title is required."
                        )
                    } else if (mDescription.isNullOrEmpty()) {
                        return BaseResponse<Photographer?>().failed(
                            message = "Service[$serviceIndex] description is required."
                        )
                    }

                    val newService = Service()
                    newService.title = title
                    newService.description = mDescription
                    newService.servicePrice = servicePrice?.toDouble() ?: 0.0
                    newService.image = if (image == null) null else FileUtils.saveImage("Service_$title", image)
                    newService.createdOn = TimeUtils.getCurrentDateTime()
                    newService.lastUpdate = TimeUtils.getCurrentDateTime()
                    services.add(serviceIndex, newService)
                }
            }
        }

        if (services.isEmpty()) {
            return BaseResponse<Photographer?>().failed(
                message = "At least one service is required."
            )
        }

        val newPhotographer = Photographer()
        newPhotographer.openAllDay = true

        val user = userService.findById(userId) ?: return BaseResponse<Photographer?>().failed(
            message = "Invalid user id."
        )

        val savedFrontIfProof = FileUtils.saveImage("Photographer_$firmName", idProofFront)
        val savedBackIfProof = FileUtils.saveImage("Photographer_$firmName", idProofBack)
        val savedPhotograph = FileUtils.saveImage("Photographer_$firmName", photograph)

        if (featuredImage != null) {
            val savedFeaturedImage = FileUtils.saveImage("Photographer_$firmName", featuredImage)
            newPhotographer.featuredImage = savedFeaturedImage
        } else {
            newPhotographer.featuredImage = user.profile
        }

        newPhotographer.userId = userId
        newPhotographer.firmName = firmName
        newPhotographer.description = description
        newPhotographer.phone = phone
        newPhotographer.email = email
        newPhotographer.idProofFront = savedFrontIfProof
        newPhotographer.idProofBack = savedBackIfProof
        newPhotographer.photograph = savedPhotograph
        newPhotographer.address = address
        newPhotographer.idProofType = idProofType
        newPhotographer.placeId = placeId
        newPhotographer.places = places
        newPhotographer.latitude = place.latitude
        newPhotographer.longitude = place.longitude
        newPhotographer.placeName = place.placeName
        newPhotographer.approvalStatus = ApprovalStatus.IN_REVIEW.value
        newPhotographer.rating = 0.0
        newPhotographer.createdOn = TimeUtils.getCurrentDateTime()
        newPhotographer.lastUpdate = TimeUtils.getCurrentDateTime()
        photographerService.save(photographer = newPhotographer)

        println("Photographer Id >>>>>>>>>>>>>>>>>> ${newPhotographer.id}")
        services.forEach {
            it.photographerId = newPhotographer.id
            servicesService.save(it)
        }

        user.photographer = false
        user.pId = newPhotographer.id
        userService.save(user)

        return BaseResponse<Photographer?>().success(
            message = "Request sent successfully.",
            data = newPhotographer
        )
    }

    @PostMapping(Endpoints.RESPOND_PHOTOGRAPHER_REQUEST)
    fun responsePhotographerRequest(
        @RequestParam("photographerId") photographerId: Long?,
        @RequestParam("status") status: String?,
        @RequestParam("reasonOfDecline") reasonOfDecline: String?,
    ): BaseResponse<Photographer?> {

        if (photographerId == null) {
            return BaseResponse<Photographer?>().failed(
                message = "Photographer id is required."
            )
        }
        if (status == null) {
            return BaseResponse<Photographer?>().failed(
                message = "Status is required."
            )
        }
        if (status !in ApprovalStatus.entries.toList().map { it.value }) {
            return BaseResponse<Photographer?>().failed(
                message = "Status '$status' is invalid."
            )
        }
        val photographer =
            photographerService.findById(id = photographerId) ?: return BaseResponse<Photographer?>().failed(
                message = "Photographer not found."
            )
//        if (status == ApprovalStatus.APPROVED.value) {
//            photographer.approvedOn = TimeUtils.getCurrentDateTime()
//            photographer.appointmentOn = true
//        }
        if (status == ApprovalStatus.DECLINED.value) {
            if (reasonOfDecline == null) {
                return BaseResponse<Photographer?>().failed(
                    message = "Reason of decline is required."
                )
            }
        }

        photographer.reasonOfDecline = reasonOfDecline
        photographer.approvalStatus = status

        try {
            val notification = Notification()
            notification.photographerId = photographerId
            notification.type = "approvalUpdate"
            notification.title = "Photographer request ${status.mkFirstUppercase()}"
            notification.description = "Your request to listed as photographer has been ${status.mkFirstUppercase()}"
            notification.markAsRead = false
            notification.createdOn = TimeUtils.getCurrentDateTime()
            notification.lastUpdate = TimeUtils.getCurrentDateTime()
            notification.sendTo = userService.findById(photographer.userId!!)?.fcm
            notificationService.save(notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        photographerService.save(photographer)

        if (status == ApprovalStatus.APPROVED.value) {
            val user = userService.findById(photographer.userId!!) ?: return BaseResponse<Photographer?>().failed(
                message = "Invalid user id."
            )
            user.photographer = true
            user.pId = photographer.id
            userService.save(user)
        }

        return BaseResponse<Photographer?>().success(
            message = if (status == ApprovalStatus.APPROVED.value) "Request approved successfully" else "Request declined.",
            data = photographer
        )
    }

    @PostMapping(Endpoints.UPDATE_PHOTOGRAPHER)
    fun updatePhotographer(
        @RequestParam("photographerId") photographerId: Long?,
        @RequestParam("firmName") firmName: String?,
        @RequestParam("featuredImage") featuredImage: MultipartFile?,
        @RequestParam("govtId") govtId: MultipartFile?,
        @RequestParam("ratePerHour") ratePerHour: Double?,
        @RequestParam("govtIdType") govtIdType: String?,
        @RequestParam("description") description: String?,
        @RequestParam("phone") phone: String?,
        @RequestParam("email") email: String?,
        @RequestParam("placeId") placeId: Long?,
        @RequestParam("places") places: String?,
        @RequestParam("timing") timing: String? = null,
        @RequestParam("openAllDay") openAllDay: String? = null,
        @RequestParam("timeSlots") timeSlots: String? = null,
        @RequestParam("admin") admin: Boolean? = null
    ): BaseResponse<Photographer?> {
        if (photographerId == null) {
            return BaseResponse<Photographer?>().failed(
                message = "Photographer id is required."
            )
        }
        if (govtIdType != null && govtIdType !in allowedGovtIdTypes) {
            return BaseResponse<Photographer?>().failed(
                message = "This id proof is not allowed."
            )
        }
        val photographer = photographerService.findById(photographerId)
            ?: return BaseResponse<Photographer?>().failed(
                message = "Invalid Id."
            )
        if (openAllDay != null) photographer.openAllDay = true
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
                return BaseResponse<Photographer?>().failed(
                    message = "Time format is incorrect."
                )
            }
            photographer.timing = timing
        }

        if (featuredImage != null) {
            val savedFeaturedImage = FileUtils.saveImage("Photographer_$firmName", featuredImage)
            photographer.featuredImage = savedFeaturedImage
        }
        if (placeId != null) {
            val place = placeService.findById(placeId)
                ?: return BaseResponse<Photographer?>().failed(
                    message = "Invalid place id."
                )
            photographer.placeId = placeId
            photographer.latitude = place.latitude
            photographer.longitude = place.longitude
        }

        if (places != null) {
            places.split(",").forEach {
                val place = placeService.findById(it.toLong())
                    ?: return BaseResponse<Photographer?>().failed(
                        message = "Invalid place id."
                    )
                photographer.latitude = place.latitude
                photographer.longitude = place.longitude
            }
            photographer.places = places
        }

        if (photographer.firmName != null) photographer.firmName = firmName
        if (photographer.description != null) photographer.description = description
        if (photographer.phone != null) photographer.phone = phone
        if (photographer.email != null) photographer.email = email
        if (photographer.ratePerHour != null) photographer.ratePerHour = ratePerHour
        if (photographer.idProofType != null) photographer.idProofType = govtIdType
        if (photographer.placeId != null) photographer.placeId = placeId
        if (photographer.places != null) photographer.places = places
        if (photographer.approvalStatus != null) photographer.approvalStatus = ApprovalStatus.IN_REVIEW.value
        if (photographer.rating != null) photographer.rating = 0.0

        photographer.lastUpdate = TimeUtils.getCurrentDateTime()
        photographerService.save(photographer = photographer)

        return BaseResponse<Photographer?>().success(
            message = "Details updated successfully.",
            data = photographer
        )
    }

    @DeleteMapping(Endpoints.DELETE_PHOTOGRAPHER)
    fun deletePhotographer(
        @RequestParam("photographerId") photographerId: Long?
    ): BaseResponse<Any?> {
        if (photographerId == null) return BaseResponse<Any?>().failed(
            message = "Photographer id is required."
        )

        try {
            photographerService.deleteById(photographerId)
            return BaseResponse<Any?>().success(
                message = "Deleted Successfully.",
                data = null
            )
        } catch (e: EmptyResultDataAccessException) {
            return BaseResponse<Any?>().failed(
                message = "Photographer with ID $photographerId not found."
            )
        } catch (e: Exception) {
            // Log the exception and handle it appropriately
            e.printStackTrace()
            println("Error deleting photographer with ID: $photographerId")
            return BaseResponse<Any?>().failed(
                message = "Failed to delete photographer."
            )
        }
    }

    @PostMapping(Endpoints.GET_PHOTOGRAPHERS_BY_PLACE_ID)
    fun getByPlaceId(
        @RequestParam("placeId") placeId: Long?,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int
    ): BaseResponse<List<Photographer>?> {
        if (placeId == null) return BaseResponse<List<Photographer>?>().failed(
            message = "Place id is required."
        )
        if (!placeService.existById(placeId)) return BaseResponse<List<Photographer>?>().failed(
            message = "Invalid Place id."
        )
        val pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "rating"))
        return try {
            val photographers = photographerService.findByPlaceId(placeId, pageable)
            BaseResponse<List<Photographer>?>().success(
                message = "Photographers fetched Successfully.",
                data = photographers?.toList() ?: emptyList(),
                mTotalPage = photographers?.totalPages ?: 0,
                page = page
            )
        } catch (e: Exception) {
            BaseResponse<List<Photographer>?>().failed(
                message = e.message
            )
        }
    }

    @PostMapping(Endpoints.GET_PHOTOGRAPHERS_ALL)
    fun getPhotographers(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int,
        @RequestParam("sortBy") sortBy: String?,
        @RequestParam("admin") admin: Boolean?,
        @RequestParam searchText: String? = null,
        @RequestParam status: String? = null,
        @RequestParam latitude: Double?,
        @RequestParam longitude: Double?,
    ): BaseResponse<List<Photographer>?> {
        val pageable = if (sortBy != null)
            PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, sortBy))
        else PageRequest.of(page - 1, perPage)
        var result: Page<Photographer>? = null
        var mStatus = status
        if (mStatus == null) {
            mStatus = ApprovalStatus.APPROVED.value
        }
        result = if (latitude != null && longitude != null && sortBy == null) {
            photographerService.findNearBy(
                latitude = latitude,
                longitude = longitude,
                searchText = searchText ?: "",
                pageable
            )
        } else {
            photographerService.findByStatus(
                mStatus,
                searchText ?: "",
                pageable
            )
        }
        return BaseResponse<List<Photographer>?>().success(
            message = "Photographers fetched successfully",
            data = result?.toList() ?: emptyList(),
            page = page,
            mTotalPage = result?.totalPages ?: 0
        )
    }

    @PostMapping(Endpoints.GET_PHOTOGRAPHERS_DETAILS)
    fun getPhotographerDetails(
        @RequestParam photographerId: Long?,
    ): BaseResponse<Photographer>? {
        if (photographerId == null) {
            return BaseResponse<Photographer>().failed(
                message = "Photographer id is required."
            )
        }
        val photographer = photographerService.findById(photographerId)
            ?: return BaseResponse<Photographer>().failed(
                message = "Invalid photographer id."
            )

        return BaseResponse<Photographer>().success(
            message = "Details fetched successfully",
            data = photographer
        )
    }

    @PostMapping(Endpoints.CHANGE_PHOTOGRAPHER_ACTIVE_STATUS)
    fun changeActiveStatus(
        @RequestParam("pId") guiderId: Long?,
        @RequestParam("active") active: Boolean?,
    ): BaseResponse<Photographer?> {
        if (guiderId == null) {
            return BaseResponse<Photographer?>().failed(
                message = "Guider id is required."
            )
        }
        val photographer =
            photographerService.findById(id = guiderId) ?: return BaseResponse<Photographer?>().failed(
                message = "Photographer not found."
            )
        photographer.active = active
        photographerService.save(photographer)

        return BaseResponse<Photographer?>().success(
            message ="Active status changed!",
            data = photographer
        )
    }

}