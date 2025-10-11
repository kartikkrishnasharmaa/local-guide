package com.local.guider.services

import com.local.guider.entities.Appointment
import com.local.guider.enumuration.AppointmentStatus
import com.local.guider.repositories.AppointmentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AppointmentService
    (
    private val appointmentRepo: AppointmentRepository
) {
    fun existsById(id: Long): Boolean {
        return appointmentRepo.existsById(id)
    }

    fun findById(id: Long): Appointment {
        return appointmentRepo.findById(id).get()
    }
    fun save(appointment: Appointment) {
        appointmentRepo.save(appointment)
    }

    fun findPageByStatusPhotographerId(
        status: String,
        photographerId: Long,
        searchText: String,
        pageable: Pageable
    ): Page<Appointment>? {
        return appointmentRepo.findByStatusPhotographerId(status, photographerId, searchText, pageable)
    }

    fun findPageByStatusGuiderId(
        status: String,
        guiderId: Long,
        searchText: String,
        pageable: Pageable
    ): Page<Appointment>? {
        return appointmentRepo.findByStatusGuiderId(status, guiderId, searchText, pageable)
    }
    fun findPageByUserId(
        userId: Long,
        searchText: String,
        pageable: Pageable
    ): Page<Appointment>? {
        return appointmentRepo.findByUserId(userId, searchText, pageable)
    }

    fun findByTransactionId(
        transactionId: String,
    ): Appointment? {
        return appointmentRepo.findByTransactionId(transactionId)
    }

     fun findGuiderIdAndUserId(
        guiderId: Long,
        userId: Long,
    ): List<Appointment>? {
        return appointmentRepo.findByGuiderIdAndUserId(AppointmentStatus.COMPLETED.key, guiderId, userId)
    }
    fun findPhotographerIdAndUserId(
        photographerId: Long,
        userId: Long,
    ): List<Appointment>? {
        return appointmentRepo.findByPhotographerIdAndUserId(AppointmentStatus.COMPLETED.key, photographerId, userId)
    }

    fun countByUserId(
        userId: Long,
    ): Long {
        return appointmentRepo.countByUserId(userId = userId)
    }

    fun countByPhotographerId(
        photographerId: Long,
    ): Long {
        return appointmentRepo.countByPhotographerId(photographerId = photographerId)
    }

    fun countByGuiderId(
        guiderId: Long,
    ): Long {
        return appointmentRepo.countByGuiderId(guiderId = guiderId)
    }

}