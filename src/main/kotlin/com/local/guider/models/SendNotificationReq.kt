package com.local.guider.models


import com.google.gson.annotations.SerializedName

data class SendNotificationReq(
    @SerializedName("message")
    var message: FcmMessage? = null,
)

data class SendNotificationToDeviceReq(
    @SerializedName("message")
    var message: FcmMessageToDevice? = null,
)

data class FcmMessage(
    @SerializedName("notification")
    var notification: NotificationReq? = null,
    @SerializedName("topic")
    var topic: String? = null
)

data class FcmMessageToDevice(
    @SerializedName("notification")
    var notification: NotificationReq? = null,
    @SerializedName("token")
    var token: String? = null
)

data class NotificationReq(
    @SerializedName("body")
    var body: String? = null,
    @SerializedName("title")
    var title: String? = null
)