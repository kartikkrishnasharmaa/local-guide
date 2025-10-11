package com.local.guider.dto

data class LoginDto(
    val phone: String,
    val password: String,
)

data class ForgetPasswordDto(
    val phoneNumber: String?,
    val password: String?
)

data class UpdateProfileDto(
    val userId: Long?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val countryCode: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?
)

data class ChangePasswordDto(
    val userId: Long?,
    val currentPassword: String?,
    val password: String?
)

data class RegisterDto(
    val name: String?,
    val phone: String?,
    val email: String?,
    val username: String?,
    val countryCode: String?,
    val password: String?,
    val address: String?,
    val gender: String?,
    val dob: String?,
    val latitude: Double?,
    val longitude: Double?
)
