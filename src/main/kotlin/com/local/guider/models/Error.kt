package com.local.guider.models


import com.google.gson.annotations.SerializedName

data class Error(
    @SerializedName("code")
    var code: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("field")
    var `field`: String? = null,
    @SerializedName("reason")
    var reason: String? = null,
    @SerializedName("source")
    var source: String? = null,
    @SerializedName("step")
    var step: String? = null
)