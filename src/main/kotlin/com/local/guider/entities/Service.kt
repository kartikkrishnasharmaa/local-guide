package com.local.guider.entities

import com.local.guider.scheme_manager.Tables
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = Tables.TABLE_SERVICES)
class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var title: String? = null
    var description: String? = null
    var image: String? = null
    var servicePrice: Double? = null
    var photographerId: Long? = null
    var guiderId: Long? = null
    var createdOn: Date? = null
    var lastUpdate: Date? = null
}