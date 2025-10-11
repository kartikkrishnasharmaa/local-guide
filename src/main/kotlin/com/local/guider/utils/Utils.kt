package com.local.guider.utils

object Utils {

    val allowedGovtIdTypes = arrayOf("Aadhaar Card", "PAN", "Driving Licence", "Passport")

    val defaultLatLng = LatLng(26.922070, 75.778885) // Jaipur

    val domain = "https://localguider.in/"

}

data class LatLng(val lat: Double, val lng: Double)