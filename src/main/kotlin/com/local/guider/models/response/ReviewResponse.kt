package com.local.guider.models.response

import java.util.*

class ReviewResponse {
    var id: Long = 0
    var userId: Long? = null
    var photographerId: Long? = null
    var guiderId: Long? = null
    var placeId: Long? = null
    var rating: Double? = null
    var message: String? = null
    var fullName: String? = null
    var profileImage: String? = null
    var createdOn: Date? = null
    var lastUpdate: Date? = null
}