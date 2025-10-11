package com.local.guider.models.response

import com.local.guider.entities.User

data class TokenResponse(
    val token: String,
    val refreshToken: String,
    val user: User
)
