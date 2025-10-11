package com.local.guider.models.response


import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("lat")
    var lat: Double? = null,
    @SerializedName("lng")
    var lng: Double? = null
)