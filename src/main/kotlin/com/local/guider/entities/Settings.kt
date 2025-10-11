package com.local.guider.entities

import com.local.guider.scheme_manager.Tables
import jakarta.persistence.*

@Entity
@Table(name = Tables.TABLE_SETTINGS)
class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    @Lob
    @Column(name = "privacyPolicy", columnDefinition = "TEXT")
    var privacyPolicy: String? = null
    @Lob
    @Column(name = "termsAndConditions", columnDefinition = "TEXT")
    var termsAndConditions: String? = null
    @Lob
    @Column(name = "aboutUs", columnDefinition = "TEXT")
    var aboutUs: String? = null

    var email: String? = null
    var phoneNumber: String? = null

    var whatsAppNumber: String? = null
    var instagram: String? = null
    var facebook: String? = null
    var twitter: String? = null

    var shareSnippet: String? = null

    var razorpayAPIKey: String? = null
    var razorpaySecretKey: String? = null

    var minimumWithdrawal: Double? = null
    var minimumAddBalance: Double? = null
    var appointmentPlatformCharge: Double? = null
    var withdrawalCharge: Double? = null

    var unreadNotificationsUser: Long? = null
    var unreadNotificationsPhotographer: Long? = null
    var unreadNotificationsGuider: Long? = null

    var totalAppointmentCharges: Double? = null

}