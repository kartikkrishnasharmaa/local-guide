package com.local.guider.entities

import com.local.guider.scheme_manager.Tables
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = Tables.TABLE_WITHDRAWAL)
class Withdrawal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var userId: Long? = null
    var photographerId: Long? = null
    var guiderId: Long? = null
    var amount: Double? = null
    var charge: Double? = null
    var amountToBeSettled: Double? = null
    var paymentStatus: String? = null
    var bankName: String? = null
    var accountNumber: String? = null
    var accountHolderName: String? = null
    var ifsc: String? = null
    var upiId: String? = null
    var useUpi: Boolean? = null
    var other: String? = null
    var createdOn: Date? = null
    var lastUpdate: Date? = null
}