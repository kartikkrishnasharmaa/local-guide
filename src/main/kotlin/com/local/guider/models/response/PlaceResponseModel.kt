package com.local.guider.models.response


import com.google.gson.annotations.SerializedName

data class PredicationResponse(
    @SerializedName("predictions")
    var predictions: List<MapPlace>? = null,
    @SerializedName("status")
    var status: String? = null
)

data class MapPlace(
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("place_id")
    var place_id: String? = null,
    @SerializedName("latitude")
    var latitude: Double? = null,
    @SerializedName("longitude")
    var longitude: Double? = null,
    @SerializedName("mapUrl")
    var mapUrl: String? = null
)