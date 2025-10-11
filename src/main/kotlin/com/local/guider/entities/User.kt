package com.local.guider.entities

import com.local.guider.scheme_manager.Tables
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = Tables.TABLE_USERS)
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var name: String = ""
    var countryCode: String = ""
    var phone: String? = null
    var email: String? = null
    var password: String = ""
    var photographer: Boolean? = false
    var rememberMe: Boolean = true
    var guider: Boolean? = false
    var address: String? = null
    var gender: String? = null
    var fcm: String? = null
    var dob: String? = null
    var isBlocked: Boolean? = null
    var reasonOfBlock: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var username: String? = null
    var profile: String? = null
    var balance: Double? = null
    var pId: Long? = null
    var gId: Long? = null
    var createdOn: Date? = null
    var lastUpdate: Date? = null

//    @JsonIgnoreProperties("userDetails", "timeSlots")
//    @OneToOne(mappedBy = "userDetails", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
//    var photographerDetails: Photographer? = null
//
//    @JsonIgnoreProperties("userDetails")
//    @OneToOne(mappedBy = "userDetails", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
//    var guiderDetails: Guider? = null
}