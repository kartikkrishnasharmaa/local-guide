package com.local.guider.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.local.guider.entities.Guider
import com.local.guider.entities.Notification
import com.local.guider.entities.Photographer
import com.local.guider.entities.Service
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
class GuiderController(
    private val placeService: PlaceService,
    private val userService: UserService,
    private val guiderService: GuiderService,
    private val servicesService: ServicesService,
    private val timeslotService: TimeslotService,
    private val notificationService: NotificationService,
) {
    
    @PostMapping(Endpoints.REQUEST_FOR_GUIDER)
    fun requestForGuider(
        request: MultipartHttpServletRequest,
    ): BaseResponse<Guider?> {

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
            return BaseResponse<Guider?>().failed(
                message = "User id is required."
            )
        }
        if (firmName == null) {
            return BaseResponse<Guider?>().failed(
                message = "Name is required."
            )
        }
        if (idProofFront == null) {
            return BaseResponse<Guider?>().failed(
                message = "Id proof front is required."
            )
        }
        if (idProofBack  == null) {
            return BaseResponse<Guider?>().failed(
                message = "Id proof back is required."
            )
        }
        if (photograph  == null) {
            return BaseResponse<Guider?>().failed(
                message = "Photograph is required."
            )
        }
        if (address  == null) {
            return BaseResponse<Guider?>().failed(
                message = "Address is required."
            )
        }
        if (phone == null) {
            return BaseResponse<Guider?>().failed(
                message = "Phone is required."
            )
        }
        if (placeId == null) {
            return BaseResponse<Guider?>().failed(
                message = "Place is required."
            )
        }
        if (places == null) {
            return BaseResponse<Guider?>().failed(
                message = "Place is required."
            )
        }
        if (idProofType !in allowedGovtIdTypes) {
            return BaseResponse<Guider?>().failed(
                message = "This id proof is not allowed."
            )
        }
        if (guiderService.existsByUserId(userId)) {
            return BaseResponse<Guider?>().failed(
                message = "Request is already exists."
            )
        }
        val place = placeService.findById(placeId)
            ?: return BaseResponse<Guider?>().failed(
                message = "Invalid place id."
            )
        val newGuider = Guider()

        val user = userService.findById(userId) ?: return BaseResponse<Guider?>().failed(
            message = "Invalid user id."
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
                        return BaseResponse<Guider?>().failed(
                            message = "Service[$serviceIndex] title is required."
                        )
                    } else if (mDescription.isNullOrEmpty()) {
                        return BaseResponse<Guider?>().failed(
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
            return BaseResponse<Guider?>().failed(
                message = "At least one service is required."
            )
        }

        val savedFrontIfProof = FileUtils.saveImage("Guider_$firmName", idProofFront)
        val savedBackIfProof = FileUtils.saveImage("Guider_$firmName", idProofBack)
        val savedPhotograph = FileUtils.saveImage("Guider_$firmName", photograph)

        if (featuredImage != null) {
            val savedFeaturedImage = FileUtils.saveImage("Guider_$firmName", featuredImage)
            newGuider.featuredImage = savedFeaturedImage
        } else {
            newGuider.featuredImage = user.profile
        }

        newGuider.userId = userId
        newGuider.firmName = firmName
        newGuider.description = description
        newGuider.phone = phone
        newGuider.email = email
//        newGuider.ratePerHour = ratePerHour
        newGuider.idProofFront = savedFrontIfProof
        newGuider.idProofBack = savedBackIfProof
        newGuider.photograph = savedPhotograph
        newGuider.address = address
        newGuider.idProofType = idProofType
        newGuider.placeId = placeId
        newGuider.places = places
        newGuider.latitude = place.latitude
        newGuider.longitude = place.longitude
        newGuider.placeName = place.placeName
        newGuider.approvalStatus = ApprovalStatus.IN_REVIEW.value
//        newGuider.userDetails = user
        newGuider.rating = 0.0
        newGuider.createdOn = TimeUtils.getCurrentDateTime()
        newGuider.lastUpdate = TimeUtils.getCurrentDateTime()
        guiderService.save(guider = newGuider)

        user.guider = false
        user.gId = newGuider.id
        userService.save(user)

        services.forEach {
            it.guiderId = newGuider.id
            servicesService.save(it)
        }

        return BaseResponse<Guider?>().success(
            message = "Request sent successfully.",
            data = newGuider
        )
    }

    @PostMapping(Endpoints.CHANGE_GUIDER_ACTIVE_STATUS)
    fun changeActiveStatus(
        @RequestParam("gId") guiderId: Long?,
        @RequestParam("active") active: Boolean?,
    ): BaseResponse<Guider?> {
        if (guiderId == null) {
            return BaseResponse<Guider?>().failed(
                message = "Guider id is required."
            )
        }
        val guider =
            guiderService.findById(id = guiderId) ?: return BaseResponse<Guider?>().failed(
                message = "Guider not found."
            )
        guider.active = active
        guiderService.save(guider)

        return BaseResponse<Guider?>().success(
            message ="Active status changed!",
            data = guider
        )
    }

    @PostMapping(Endpoints.RESPOND_GUIDER_REQUEST)
    fun responseGuiderRequest(
        @RequestParam("guiderId") guiderId: Long?,
        @RequestParam("status") status: String?,
        @RequestParam("reasonOfDecline") reasonOfDecline: String?,
    ): BaseResponse<Guider?> {

        if (guiderId == null) {
            return BaseResponse<Guider?>().failed(
                message = "Guider id is required."
            )
        }
        if (status == null) {
            return BaseResponse<Guider?>().failed(
                message = "Status is required."
            )
        }
        if (status !in ApprovalStatus.entries.toList().map { it.value }) {
            return BaseResponse<Guider?>().failed(
                message = "Status '$status' is invalid."
            )
        }
        val guider =
            guiderService.findById(id = guiderId) ?: return BaseResponse<Guider?>().failed(
                message = "Guider not found."
            )
//        if (status == ApprovalStatus.APPROVED.value) {
//            guider.approvedOn = TimeUtils.getCurrentDateTime()
//            guider.appointmentOn = true
//        }
        if (status == ApprovalStatus.DECLINED.value) {
            if (reasonOfDecline == null) {
                return BaseResponse<Guider?>().failed(
                    message = "Reason of decline is required."
                )
            }
        }
        guider.reasonOfDecline = reasonOfDecline
        guider.approvalStatus = status

        try {
            val notification = Notification()
            notification.photographerId = guiderId
            notification.type = "approval update"
            notification.title = "Guider request ${status.mkFirstUppercase()}"
            notification.description = "Your request to listed as guider has been ${status.mkFirstUppercase()}"
            notification.markAsRead = false
            notification.createdOn = TimeUtils.getCurrentDateTime()
            notification.lastUpdate = TimeUtils.getCurrentDateTime()
            notification.sendTo = userService.findById(guider.userId!!)?.fcm
            notificationService.save(notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        guiderService.save(guider)

        if (status == ApprovalStatus.APPROVED.value) {
            val user = userService.findById(guider.userId!!) ?: return BaseResponse<Guider?>().failed(
                message = "Invalid user id."
            )
            user.guider = true
            user.gId = guider.id
            userService.save(user)
        }

        return BaseResponse<Guider?>().success(
            message = if (status == ApprovalStatus.APPROVED.value) "Request approved successfully" else "Request declined.",
            data = guider
        )
    }

    @PostMapping(Endpoints.UPDATE_GUIDER)
    fun updateGuider(
        @RequestParam("guiderId") guiderId: Long?,
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
    ): BaseResponse<Guider?> {
        if (guiderId == null) {
            return BaseResponse<Guider?>().failed(
                message = "Guider id is required."
            )
        }
        if (govtIdType != null && govtIdType !in allowedGovtIdTypes) {
            return BaseResponse<Guider?>().failed(
                message = "This id proof is not allowed."
            )
        }
        val guider = guiderService.findById(guiderId)
            ?: return BaseResponse<Guider?>().failed(
                message = "Invalid Id."
            )
        if (openAllDay != null) guider.openAllDay = true
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
                return BaseResponse<Guider?>().failed(
                    message = "Time format is incorrect."
                )
            }
            guider.timing = timing
        }

        if (featuredImage != null) {
            val savedFeaturedImage = FileUtils.saveImage("guider_$firmName", featuredImage)
            guider.featuredImage = savedFeaturedImage
        }
        if (placeId != null) {
            val place = placeService.findById(placeId)
                ?: return BaseResponse<Guider?>().failed(
                    message = "Invalid place id."
                )
            guider.placeId = placeId
            guider.latitude = place.latitude
            guider.longitude = place.longitude
        }

        if (places != null) {
            places.split(",").forEach {
                val place = placeService.findById(it.toLong())
                    ?: return BaseResponse<Guider?>().failed(
                        message = "Invalid place id."
                    )
                guider.latitude = place.latitude
                guider.longitude = place.longitude
            }
            guider.places = places
        }

        if (guider.firmName != null) guider.firmName = firmName
        if (guider.description != null) guider.description = description
        if (guider.phone != null) guider.phone = phone
        if (guider.email != null) guider.email = email
        if (guider.ratePerHour != null) guider.ratePerHour = ratePerHour
        if (guider.idProofType != null) guider.idProofType = govtIdType
        if (guider.placeId != null) guider.placeId = placeId
        if (guider.places != null) guider.places = places
        if (guider.approvalStatus != null) guider.approvalStatus = ApprovalStatus.IN_REVIEW.value
        if (guider.rating != null) guider.rating = 0.0

        guider.lastUpdate = TimeUtils.getCurrentDateTime()
        guiderService.save(guider = guider)

        return BaseResponse<Guider?>().success(
            message = "Details updated successfully.",
            data = guider
        )
    }

    @DeleteMapping(Endpoints.DELETE_GUIDER)
    fun deleteGuider(
        @RequestParam("guiderId") guiderId: Long?
    ): BaseResponse<Any?> {
        if (guiderId == null) return BaseResponse<Any?>().failed(
            message = "Guider id is required."
        )

        try {
            guiderService.deleteById(guiderId)
            return BaseResponse<Any?>().success(
                message = "Deleted Successfully.",
                data = null
            )
        } catch (e: EmptyResultDataAccessException) {
            return BaseResponse<Any?>().failed(
                message = "Guider with ID $guiderId not found."
            )
        } catch (e: Exception) {
            // Log the exception and handle it appropriately
            e.printStackTrace()
            println("Error deleting guider with ID: $guiderId")
            return BaseResponse<Any?>().failed(
                message = "Failed to delete guider."
            )
        }
    }
    @PostMapping(Endpoints.GET_GUIDERS_BY_PLACE_ID)
    fun getByPlaceId(
        @RequestParam("placeId") placeId: Long?,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int
    ): BaseResponse<List<Guider>?> {
        if (placeId == null) return BaseResponse<List<Guider>?>().failed(
            message = "Place id is required."
        )
        if (!placeService.existById(placeId)) return BaseResponse<List<Guider>?>().failed(
            message = "Invalid Place id."
        )
        val pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "rating"))
        val guiders = guiderService.findByPlaceId(placeId, pageable)
        return BaseResponse<List<Guider>?>().success(
            message = "Guiders fetched Successfully.",
            data = guiders?.toList() ?: emptyList(),
            mTotalPage = guiders?.totalPages ?: 0,
            page = page
        )
    }

    @PostMapping(Endpoints.GET_GUIDERS_ALL)
    fun getGuiders(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int,
        @RequestParam("sortBy") sortBy: String?,
        @RequestParam("admin") admin: Boolean?,
        @RequestParam searchText: String? = null,
        @RequestParam status: String? = null,
        @RequestParam latitude: Double?,
        @RequestParam longitude: Double?,
    ): BaseResponse<List<Guider>?> {
        val pageable = if (sortBy != null)
            PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, sortBy))
        else PageRequest.of(page - 1, perPage)
        var result: Page<Guider>? = null
        result = if (latitude != null && longitude != null && sortBy == null) {
            guiderService.findNearBy(
                latitude = latitude,
                longitude = longitude,
                searchText = searchText ?: "",
                pageable
            )
        } else {
            guiderService.findByStatus(
                status = status ?: ApprovalStatus.APPROVED.value,
                searchText = searchText ?: "",
                pageable
            )
        }
        return BaseResponse<List<Guider>?>().success(
            message = "Guiders fetched successfully",
            data = result?.toList() ?: emptyList(),
            page = page,
            mTotalPage = result?.totalPages ?: 0
        )
    }

    @PostMapping(Endpoints.GET_GUIDERS_DETAILS)
    fun getGuiderDetails(
        @RequestParam guiderId: Long?,
    ): BaseResponse<Guider>? {
        if (guiderId == null) {
            return BaseResponse<Guider>().failed(
                message = "Guider id is required."
            )
        }
        val guider = guiderService.findById(guiderId)
            ?: return BaseResponse<Guider>().failed(
                message = "Invalid guider id."
            )

        return BaseResponse<Guider>().success(
            message = "Details fetched successfully",
            data = guider
        )
    }

}