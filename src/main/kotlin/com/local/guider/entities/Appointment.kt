package com.local.guider.entities

import com.local.guider.scheme_manager.Tables
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = Tables.TABLE_APPOINTMENT)
class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var userId: Long? = null
    var photographerId: Long? = null
    var guiderId: Long? = null
    var date: Date? = null
    var serviceName: String? = null
    var serviceImage: String? = null
    var appointmentCharge: Double? = null
    var serviceCost: Double? = null
    var paymentStatus: String? = null
    var transactionId: String? = null
    var cancellationReason: String? = null
    var note: String? = null
    var appointmentStatus: String? = null
    var totalPayment: Double? = null
    var createdOn: Date? = null
    var lastUpdate: Date? = null

//    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
//    @JoinColumn(name = "appointment_id")
//    var timeSlots: List<Timeslot>? = null

}