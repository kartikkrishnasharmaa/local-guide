package com.local.guider.repositories

import com.local.guider.entities.Timeslot
import org.springframework.data.jpa.repository.JpaRepository

interface TimeslotRepository: JpaRepository<Timeslot, Long> {

}