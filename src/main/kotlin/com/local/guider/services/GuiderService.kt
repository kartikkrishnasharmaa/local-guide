package com.local.guider.services

import com.local.guider.entities.Guider
import com.local.guider.repositories.GuiderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GuiderService
    (
    private val guiderRepo: GuiderRepository
) {
    fun existsById(id: Long): Boolean {
        return guiderRepo.existsById(id)
    }
    fun findAll(): List<Guider> {
        return guiderRepo.findAll()
    }
    fun count(): Long {
        return guiderRepo.count()
    }
    fun existsByUserId(id: Long): Boolean {
        return guiderRepo.existsByUserId(id) ?: false
    }

    fun findById(id: Long): Guider? {
        return guiderRepo.findByIdOrNull(id)
    }

    fun save(guider: Guider) {
        guiderRepo.save(guider)
    }

    fun deleteById(id: Long) {
        guiderRepo.deleteById(id)
    }

    fun findByPage(pageable: Pageable): Page<Guider> {
        return guiderRepo.findAll(pageable)
    }

    fun findApproved(pageable: Pageable): Page<Guider>? {
        return guiderRepo.findApproved(pageable)
    }

    fun findByStatus(status: String, searchText: String, pageable: Pageable): Page<Guider>? {
        return guiderRepo.findByStatus(status, searchText, pageable)
    }

    fun findByPlaceId(placeId: Long, pageable: Pageable): Page<Guider>? {
        return guiderRepo.findByPlaceIds(placeId, pageable)
    }

    fun findNearBy(latitude: Double, longitude: Double, searchText: String, pageable: Pageable): Page<Guider>? {
        return guiderRepo.findNearbyGuiders(latitude, longitude, searchText, pageable)
    }

}