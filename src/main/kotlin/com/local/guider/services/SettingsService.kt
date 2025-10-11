package com.local.guider.services

import com.local.guider.entities.Settings
import com.local.guider.repositories.SettingsRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SettingsService(
    private val settingsRepo: SettingsRepository,
    @Value("\${razorpay.api.key}") private val razorpayAPIKey: String,
    @Value("\${razorpay.api.secret_key}") private val razorpaySecretKey: String,
) {

    fun save(settings: Settings) {
        settingsRepo.save(settings)
    }

    fun getFirst(): Settings {
        val all = settingsRepo.findAll()
        val settings = if (all.isEmpty()) Settings() else all.first()
        settings.razorpayAPIKey = razorpayAPIKey
        settings.razorpaySecretKey = razorpaySecretKey
        return settings
    }

}