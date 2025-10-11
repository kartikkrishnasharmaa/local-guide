package com.local.guider.models.response

import com.google.gson.annotations.SerializedName

data class LatLngResponse(
    @SerializedName("result")
    var result: LatLngResult? = null
)

data class LatLngResult(
    @SerializedName("geometry")
    var geometry: Geometry? = null,
    @SerializedName("url")
    var url: String? = null,
)

data class Geometry(
    @SerializedName("location")
    var location: Location? = null
)