package com.local.guider.services

import com.local.guider.entities.User
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit


@Service
class TokenService(
    private val jwtDecoder: JwtDecoder,
    private val jwtEncoder: JwtEncoder,
    private val userService: UserService,
) {

    // This function will create both an access token and a refresh token
    fun createTokens(user: User, rememberMe: Boolean = false): Pair<String, String> {
        val accessToken = createAccessToken(user)
        val refreshToken = createRefreshToken(user, rememberMe)
        return Pair(accessToken, refreshToken)
    }

    fun createAccessToken(user: User): String {
        val jwsHeader = JwsHeader.with { "HS256" }.build()
        val expirationTime = if (user.rememberMe) {
            Instant.now().plus(30L, ChronoUnit.DAYS) // Longer expiration for "Remember Me"
        } else {
            Instant.now().plus(1L, ChronoUnit.HOURS) // Shorter expiration for regular access
        }
        val claims = JwtClaimsSet.builder()
                .issuedAt(Instant.now())
                .expiresAt(expirationTime)
                .subject(user.name)
                .claim("userId", user.id)
                .build()
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).tokenValue
    }

    private fun createRefreshToken(user: User, rememberMe: Boolean): String {
        val jwsHeader = JwsHeader.with { "HS256" }.build()
        val expirationTime = if (rememberMe) {
            Instant.now().plus(30L, ChronoUnit.DAYS) // Longer expiration for "Remember Me"
        } else {
            Instant.now().plus(7L, ChronoUnit.DAYS) // Shorter expiration for regular refresh tokens
        }
        val claims = JwtClaimsSet.builder()
                .issuedAt(Instant.now())
                .expiresAt(expirationTime)
                .subject(user.name)
                .claim("userId", user.id)
                .claim("refreshToken", true) // A marker to identify refresh tokens
                .build()
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).tokenValue
    }

    fun parseToken(token: String): User? {
        return try {
            val jwt = jwtDecoder.decode(token)
            val userId = jwt.claims["userId"] as Long
            userService.findById(userId)
        } catch (e: Exception) {
            null
        }
    }

    fun parseRefreshToken(refreshToken: String): User? {
        return try {
            val jwt = jwtDecoder.decode(refreshToken)
            val isRefreshToken = jwt.claims["refreshToken"] as? Boolean
            if (isRefreshToken == true) {
                val userId = jwt.claims["userId"] as Long
                userService.findById(userId)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

}