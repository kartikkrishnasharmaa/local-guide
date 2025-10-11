package com.local.guider.services

import com.google.auth.oauth2.GoogleCredentials
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.concurrent.locks.ReentrantLock

@Service
class FirebaseAuthService {

    private val lock = ReentrantLock()
    private var cachedToken: String? = null
    private var tokenExpiryTime: Long = 0

    private fun generateNewToken(): String {
        val inputStream: InputStream = ClassPathResource("firebase-service-account.json").inputStream
        val credentials = GoogleCredentials.fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

        credentials.refreshIfExpired()
        return credentials.accessToken.tokenValue.also {
            cachedToken = it
            tokenExpiryTime = System.currentTimeMillis() + (credentials.accessToken.expirationTime.time - System.currentTimeMillis()) - 60000 // Refresh 1 min before expiry
        }
    }

    fun getAccessToken(): String {
        lock.lock()
        try {
            return if (cachedToken == null || System.currentTimeMillis() >= tokenExpiryTime) {
                generateNewToken()
            } else {
                cachedToken!!
            }
        } finally {
            lock.unlock()
        }
    }
}
