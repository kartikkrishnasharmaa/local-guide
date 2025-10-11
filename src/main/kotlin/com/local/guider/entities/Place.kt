package com.local.guider.entities

import com.local.guider.scheme_manager.Tables
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = Tables.TABLE_PLACES)
class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var placeName: String? = null
    var featuredImage: String? = null
    var state: String? = null
    var city: String? = null
    var latitude: Double? = null
    var isTop: Boolean? = null
    var longitude: Double? = null
    var fullAddress: String? = null
    var mapUrl: String? = null
    var rating: Double? = null
    var views: Long? = null
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    var description: String? = null
    var timing: String? = null
    var openAllDay: Boolean? = null
    var createdOn: Date? = null
    var lastUpdate: Date? = null
}