package com.local.guider.dto

import java.util.*

data class CreateAppointmentDto(
    var userId: Long,
    var photographerId: Long? = null,
    var guiderId: Long? = null,
    var dateTime: String? = null,
    var transactionId: String? = null,
    var serviceId: Long? = null,
    var appointmentCharge: Double? = null,
    var serviceCost: Double? = null,
    var totalPayment: Double? = null,
    var note: String? = null,
    var paymentStatus: String? = null,
)

class AppointmentResponse {
    var id: Long = 0
    var userId: Long? = null
    var photographerId: Long? = null
    var guiderId: Long? = null
    var date: Date? = null
    var customerName: String? = null
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
}