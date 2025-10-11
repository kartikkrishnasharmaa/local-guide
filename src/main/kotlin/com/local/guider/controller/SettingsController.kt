package com.local.guider.controller

import com.local.guider.entities.Settings
import com.local.guider.models.BaseResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.services.*
import org.springframework.web.bind.annotation.*
import java.util.Date

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class SettingsController(
    private val settingsService: SettingsService,
    private val notificationService: NotificationService,
    private val userService: UserService,
    private val guiderService: GuiderService,
    private val photographerService: PhotographerService
) {

    @PostMapping(Endpoints.UPDATE_SETTINGS)
    fun updateSettings(
        @RequestBody payload: Settings
    ): BaseResponse<Settings> {
        val existingSetting = settingsService.getFirst()
        if (payload.privacyPolicy != null) {
            existingSetting.privacyPolicy = payload.privacyPolicy
        }
        if (payload.termsAndConditions != null) {
            existingSetting.termsAndConditions = payload.termsAndConditions
        }
        if (payload.aboutUs != null) {
            existingSetting.aboutUs = payload.aboutUs
        }
        if (payload.email != null) {
            existingSetting.email = payload.email
        }
        if (payload.phoneNumber != null) {
            existingSetting.phoneNumber = payload.phoneNumber
        }
        if (payload.whatsAppNumber != null) {
            existingSetting.whatsAppNumber = payload.whatsAppNumber
        }
        if (payload.instagram != null) {
            existingSetting.instagram = payload.instagram
        }
        if (payload.facebook != null) {
            existingSetting.facebook = payload.facebook
        }
        if (payload.twitter != null) {
            existingSetting.twitter = payload.twitter
        }
        if (payload.minimumWithdrawal != null) {
            existingSetting.minimumWithdrawal = payload.minimumWithdrawal
        }
        if (payload.minimumAddBalance != null) {
            existingSetting.minimumAddBalance = payload.minimumAddBalance
        }
        if (payload.shareSnippet != null) {
            existingSetting.shareSnippet = payload.shareSnippet
        }
        if (payload.appointmentPlatformCharge != null) {
            existingSetting.appointmentPlatformCharge = payload.appointmentPlatformCharge
        }
        if (payload.withdrawalCharge != null) {
            existingSetting.withdrawalCharge = payload.withdrawalCharge
        }
        settingsService.save(existingSetting)
        return BaseResponse<Settings>().success(
            data = existingSetting,
            message = "Settings updated successfully"
        )
    }

    @PostMapping(Endpoints.GET_SETTINGS)
    fun getSettings(
        @RequestParam("userId") userId: Long?,
        @RequestParam("photographerId") photographerId: Long?,
        @RequestParam("guiderId") guiderId: Long?
    ): BaseResponse<Settings> {

        val settings = settingsService.getFirst()

        if(userId != null) {
            val user = userService.findById(userId)
            settings.unreadNotificationsUser = notificationService.countUnreadByUserId(userId, user?.createdOn ?: Date())
        }
        if(photographerId != null) {
            val photographer = photographerService.findById(photographerId)
            settings.unreadNotificationsPhotographer = notificationService.countUnreadByPhotographerId(photographerId, photographer?.createdOn ?: Date())
        }
        if(guiderId != null) {
            val guider = guiderService.findById(guiderId)
            settings.unreadNotificationsGuider = notificationService.countUnreadByGuiderId(guiderId, guider?.createdOn ?: Date())
        }

        return BaseResponse<Settings>().success(
            data = settings,
            message = "Settings fetched successfully."
        )
    }

}