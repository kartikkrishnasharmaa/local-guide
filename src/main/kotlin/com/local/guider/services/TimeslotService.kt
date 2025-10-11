package com.local.guider.services

import com.local.guider.entities.Timeslot
import com.local.guider.repositories.TimeslotRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TimeslotService(
    private val timeslotRepo: TimeslotRepository
) {
    fun existById(id: Long): Boolean {
        return timeslotRepo.existsById(id)
    }
    fun findById(id: Long): Timeslot? {
        return timeslotRepo.findByIdOrNull(id)
    }
    fun findAll(): List<Timeslot>? {
        return timeslotRepo.findAll()
    }
    fun save(timeslot: Timeslot): Timeslot? {
        return timeslotRepo.save(timeslot)
    }
    fun deleteById(id: Long) {
        return timeslotRepo.deleteById(id)
    }
}