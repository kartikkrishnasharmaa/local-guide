package com.local.guider.services

import com.local.guider.entities.Notification
import com.local.guider.models.*
import com.local.guider.repositories.NotificationRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class NotificationService(
    private val restTemplate: RestTemplate,
    @Value("\${fcm.api.url}") private val fcmApiUrl: String,
    private val firebaseAuthService: FirebaseAuthService,
    private val notificationRepo: NotificationRepository
) {

    fun save(notification: Notification, sendNotification: Boolean = true) {
        if (sendNotification && notification.sendTo != null && !notification.sendTo.isNullOrEmpty()) {
            val accessToken = firebaseAuthService.getAccessToken()
            val headers = HttpHeaders()
            headers.set("Authorization", "Bearer $accessToken")
            headers.set("Content-Type", "application/json")

            val request = SendNotificationReq(
                message = FcmMessage(
                    topic = notification.sendTo,
                    notification = NotificationReq(
                        title = notification.title,
                        body = notification.description
                    )
                )
            )

//            val fcmRequest = SendNotificationToDeviceReq(
//                message = FcmMessageToDevice(
//                    token = notification.sendTo,
//                    notification = NotificationReq(
//                        title = notification.title,
//                        body = notification.description
//                    )
//                )
//            )

            val requestEntity = HttpEntity(request, headers)

            try {
                val response = restTemplate.exchange(fcmApiUrl, HttpMethod.POST, requestEntity, Any::class.java)
                println("FCM Response: >>>>>>>>>>>>>>>> $response")
            } catch (ex: Exception) {
                println("FCM Error: ${ex.message}, Full Error: $ex") //Include the full error.
            }
        } else {
            println("FCM token is null or empty, skipping FCM send.")
        }

        notificationRepo.save(notification)
    }

    fun getById(id: Long): Notification? {
        return notificationRepo.findById(id).getOrElse { null }
    }

    fun deleteById(id: Long) {
        notificationRepo.deleteById(id)
    }

    fun findByPhotographerId(photographerId: Long, userCreatedDate: Date, pageable: Pageable): Page<Notification> {
        return notificationRepo.findByPhotographerId(photographerId, userCreatedDate, pageable)
    }

    fun findByGuiderId(guiderId: Long, userCreatedDate: Date, pageable: Pageable): Page<Notification> {
        return notificationRepo.findByGuiderId(guiderId, userCreatedDate, pageable)
    }

    fun findByUserId(userId: Long, userCreatedDate: Date, pageable: Pageable): Page<Notification> {
        return notificationRepo.findByUserId(userId, userCreatedDate, pageable)
    }

    fun findAllByAdmin(pageable: Pageable): Page<Notification> {
        return notificationRepo.findAllByAdmin(pageable)
    }

    fun countUnreadByPhotographerId(photographerId: Long, userCreatedDate: Date): Long {
        return notificationRepo.countUnreadByPhotographerId(photographerId, userCreatedDate)
    }

    fun countUnreadByGuiderId(guiderId: Long, userCreatedDate: Date): Long {
        return notificationRepo.countUnreadByGuiderId(guiderId, userCreatedDate)
    }

    fun countUnreadByUserId(userId: Long, userCreatedDate: Date): Long {
        return notificationRepo.countUnreadByUserId(userId, userCreatedDate)
    }

}