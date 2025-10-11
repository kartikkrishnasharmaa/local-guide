package com.local.guider.entities

import com.local.guider.scheme_manager.Tables
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = Tables.TABLE_REVIEW)
class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var userId: Long? = null
    var photographerId: Long? = null
    var guiderId: Long? = null
    var placeId: Long? = null
    var rating: Double? = null
    var newRating: Double? = null
    var message: String? = null
    var createdOn: Date? = null
    var lastUpdate: Date? = null
}