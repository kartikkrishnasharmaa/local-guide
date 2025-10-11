package com.local.guider.entities

import com.local.guider.scheme_manager.Tables
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = Tables.TABLE_TRANSACTION)
class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var userId: Long? = null
    var appointmentId: Long? = null
    var photographerId: Long? = null
    var guiderId: Long? = null
    var amount: Double? = null
    var charge: Double? = null
    var totalAmount: Double? = null
    var paymentFor: String? = null
    var isCredit: Boolean? = null
    var paymentStatus: String? = null
    var transactionId: String? = null
    var paymentToken: String? = null
    var other: String? = null
    var gateway: String? = null
    var createdOn: Date? = null
    var lastUpdate: Date? = null
}