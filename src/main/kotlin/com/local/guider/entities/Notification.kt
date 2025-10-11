package com.local.guider.entities

import com.local.guider.scheme_manager.Tables
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = Tables.TABLE_NOTIFICATIONS)
class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var title: String? = null
    var description: String? = null
    var type: String? = null
    var sendTo: String? = null
    var forAll: Boolean? = null
    var fromAdmin: Boolean? = null
    var forPhotographers: Boolean? = null
    var forGuiders: Boolean? = null
    var forUsers: Boolean? = null
    var markAsRead: Boolean? = null
    var userId: Long? = null
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    var note: String? = null
    var photographerId: Long? = null
    var guiderId: Long? = null
    var createdOn: Date? = null
    var lastUpdate: Date? = null
}