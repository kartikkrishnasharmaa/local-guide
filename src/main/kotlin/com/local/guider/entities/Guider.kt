package com.local.guider.entities

import com.local.guider.scheme_manager.Tables
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = Tables.TABLE_GUIDER)
class Guider {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var userId: Long? = null
    var firmName: String? = null
    var description: String? = null
    var phone: String? = null
    var email: String? = null
    var rating: Double? = null
    var currentPlanId: Long? = null
    var balance: Double? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var placeName: String? = null
    var ratePerHour: Double? = null
    var idProofFront: String? = null
    var idProofBack: String? = null
    var photograph: String? = null
    var active: Boolean? = null
    var address: String? = null
    var placeId: Long? = null
    var places: String? = null
    var featuredImage: String? = null
    var idProofType: String? = null
    var approvalStatus: String? = null
    var reasonOfDecline: String? = null
    var approvedOn: Date? = null
    var createdOn: Date? = null
    var lastUpdate: Date? = null
//    var appointmentOn: Boolean? = null
    var openAllDay: Boolean? = null
    var timing: String? = null

//    @JsonIgnoreProperties("guider", "photographer")
//    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
//    @JoinColumn(name = "guider_id")
//    var timeSlots: List<Timeslot>? = null
//
//    @JsonIgnoreProperties("guiderDetails", "photographerDetails")
//    @OneToOne(targetEntity = User::class)
//    @JoinColumn(name = "userId", referencedColumnName = "id", insertable = false, updatable = false)
//    var userDetails: User? = null
}