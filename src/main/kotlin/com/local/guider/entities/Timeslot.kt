package com.local.guider.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.local.guider.scheme_manager.Tables
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = Tables.TABLE_TIME_SLOTS)
class Timeslot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var startTime: Date? = null
    var endTime: Date? = null
    var title: String? = null
    var createdOn: Date? = null
    var lastUpdate: Date? = null

    @ManyToOne
    @JoinColumn(name = "photographer_id")
    @JsonBackReference
    var photographer: Photographer? = null

    @ManyToOne
    @JoinColumn(name = "guider_id")
    @JsonBackReference
    var guider: Guider? = null

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    @JsonBackReference
    var appointment: Appointment? = null

}