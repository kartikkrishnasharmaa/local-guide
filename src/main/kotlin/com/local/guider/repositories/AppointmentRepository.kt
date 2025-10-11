package com.local.guider.repositories

import com.local.guider.entities.Appointment
import com.local.guider.entities.Photographer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AppointmentRepository: JpaRepository<Appointment, Long> {

    @Query(
        "SELECT a FROM Appointment a WHERE a.appointmentStatus = :status AND a.photographerId = :photographerId " +
                "AND (:searchText IS NULL OR LOWER(TRIM(a.serviceName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')))"
    )
    fun findByStatusPhotographerId(status: String, photographerId: Long, searchText: String?, pageable: Pageable): Page<Appointment>?

    @Query(
        "SELECT a FROM Appointment a WHERE a.appointmentStatus = :status AND a.guiderId = :guiderId " +
                "AND (:searchText IS NULL OR LOWER(TRIM(a.serviceName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')))"
    )
    fun findByStatusGuiderId(status: String, guiderId: Long, searchText: String?, pageable: Pageable): Page<Appointment>?

    @Query(
        "SELECT a FROM Appointment a WHERE a.userId = :userId " +
                "AND (:searchText IS NULL OR LOWER(TRIM(a.serviceName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')))"
    )
    fun findByUserId(userId: Long, searchText: String?, pageable: Pageable): Page<Appointment>?

    @Query(
        "SELECT a FROM Appointment a WHERE a.transactionId = :transactionId"
    )
    fun findByTransactionId(transactionId: String): Appointment?

    @Query(
        "SELECT a FROM Appointment a WHERE a.appointmentStatus = :status AND a.photographerId = :photographerId AND a.userId = :userId"
    )
    fun findByPhotographerIdAndUserId(status: String, photographerId: Long, userId: Long): List<Appointment>

     @Query(
        "SELECT a FROM Appointment a WHERE a.appointmentStatus = :status AND a.guiderId = :guiderId AND a.userId = :userId"
    )
    fun findByGuiderIdAndUserId(status: String, guiderId: Long, userId: Long): List<Appointment>

    @Query(
        "SELECT COUNT(a) FROM Appointment a WHERE a.userId = :userId"
    )
    fun countByUserId(userId: Long): Long

    @Query(
        "SELECT COUNT(a) FROM Appointment a WHERE a.photographerId = :photographerId AND a.appointmentStatus = 'success'"
    )
    fun countByPhotographerId(photographerId: Long): Long

    @Query(
        "SELECT COUNT(a) FROM Appointment a WHERE a.guiderId = :guiderId AND a.appointmentStatus = 'success'"
    )
    fun countByGuiderId(guiderId: Long): Long

}