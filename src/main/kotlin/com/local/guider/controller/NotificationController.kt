package com.local.guider.controller

import com.local.guider.entities.Notification
import com.local.guider.models.BaseResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.services.GuiderService
import com.local.guider.services.NotificationService
import com.local.guider.services.PhotographerService
import com.local.guider.services.UserService
import com.local.guider.utils.TimeUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class NotificationController(
    private val notificationService: NotificationService,
    private val userService: UserService,
    private val guiderService: GuiderService,
    private val photographerService: PhotographerService
) {

    @PostMapping(Endpoints.GET_NOTIFICATIONS)
    fun getNotifications(
        @RequestParam("photographerId") photographerId: Long? = null,
        @RequestParam("guiderId") guiderId: Long? = null,
        @RequestParam("userId") userId: Long? = null,
        @RequestParam("admin") admin: Boolean? = null,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int
    ): BaseResponse<List<Notification>>? {

        if (photographerId == null && guiderId == null && userId == null && admin != true) {
            return BaseResponse<List<Notification>>().failed("No Id specified")
        }

        val pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "lastUpdate"))

        val result: List<Notification> = if (photographerId != null) {
            val photographer = photographerService.findById(photographerId)
            notificationService.findByPhotographerId(photographerId, photographer?.createdOn ?: Date(), pageable)
                .toList()
        } else if (guiderId != null) {
            val guider = guiderService.findById(guiderId)
            notificationService.findByGuiderId(guiderId, guider?.createdOn ?: Date(), pageable).toList()
        } else if (userId != null) {
            val user = userService.findById(userId)
            notificationService.findByUserId(userId, user?.createdOn ?: Date(), pageable).toList()
        } else if (admin == true) {
            notificationService.findAllByAdmin(pageable).toList()
        } else {
            ArrayList()
        }

        return BaseResponse<List<Notification>>().success(
            message = "Notifications fetched successfully.",
            data = result
        )
    }

    @PostMapping(Endpoints.CREATE_NOTIFICATION)
    fun createNotification(
        @RequestBody payload: Notification?
    ): BaseResponse<Notification> {
        var sendTo: String?
        if (payload == null) {
            return BaseResponse<Notification>().failed("Invalid payload")
        }
        if (payload.title == null) {
            return BaseResponse<Notification>().failed("Title is required")
        }
        if (payload.description == null) {
            return BaseResponse<Notification>().failed("Description is required")
        }
        if (payload.photographerId == null && payload.guiderId == null && payload.userId == null && payload.forGuiders != true && payload.forPhotographers != true && payload.forUsers != null) {
            payload.forAll = true
            sendTo = "all"
        }
        if (payload.photographerId != null) {
            val pUser = userService.findByPId(payload.photographerId!!)
            sendTo = pUser?.fcm
        } else if (payload.guiderId != null) {
            val gUser = userService.findByGId(payload.guiderId!!)
            sendTo = gUser?.fcm
        } else if (payload.userId != null) {
            val user = userService.findById(payload.userId!!)
            sendTo = user?.fcm
        } else if (payload.forUsers == true) {
            sendTo = "visitors"
        } else if (payload.forGuiders == true) {
            sendTo = "guiders"
        } else if (payload.forPhotographers == true) {
            sendTo = "photographers"
        } else {
            sendTo = "all"
        }

        payload.createdOn = TimeUtils.getCurrentDateTime()
        payload.lastUpdate = TimeUtils.getCurrentDateTime()
        payload.fromAdmin = true
        payload.sendTo = sendTo

        notificationService.save(payload)
        return BaseResponse<Notification>().success(
            "Notification sent successfully",
            data = payload
        )
    }

    @PostMapping(Endpoints.MARK_AS_READ_NOTIFICATION)
    fun markAsRead(
        @RequestParam notificationId: Long
    ): BaseResponse<Notification> {
        val notification = notificationService.getById(notificationId)
            ?: return BaseResponse<Notification>().failed("Notification not found")
        notification.markAsRead = true
        notificationService.save(notification, sendNotification = false)
        return BaseResponse<Notification>().success(
            "Notification marked as read",
            data = notification
        )
    }

    @PostMapping(Endpoints.DELETE_NOTIFICATION)
    fun deleteNotification(
        @RequestParam notificationId: Long
    ): BaseResponse<Notification> {
        val notification = notificationService.getById(notificationId)
            ?: return BaseResponse<Notification>().failed("Notification not found")
        notificationService.deleteById(notificationId)
        return BaseResponse<Notification>().success(
            "Notification deleted successfully",
            data = notification
        )
    }

}