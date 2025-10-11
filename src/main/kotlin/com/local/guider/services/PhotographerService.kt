package com.local.guider.services

import com.local.guider.entities.Photographer
import com.local.guider.repositories.PhotographerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PhotographerService(
    private val photographerRepo: PhotographerRepository
) {
    fun existsById(id: Long): Boolean {
        return photographerRepo.existsById(id)
    }

    fun findAll(): List<Photographer> {
        return photographerRepo.findAll()
    }

    fun existsByUserId(id: Long): Boolean {
        return photographerRepo.existsByUserId(id) ?: false
    }

    fun findById(id: Long): Photographer? {
        return photographerRepo.findByIdOrNull(id)
    }

    fun save(photographer: Photographer) {
        photographerRepo.save(photographer)
    }

    fun deleteById(id: Long) {
        photographerRepo.deleteById(id)
    }

    fun findByPage(pageable: Pageable): Page<Photographer> {
       return photographerRepo.findAll(pageable)
    }
    fun count(): Long {
       return photographerRepo.count()
    }
    fun findApproved(pageable: Pageable): Page<Photographer>? {
       return photographerRepo.findApproved(pageable)
    }

    fun findByStatus(status: String, searchText: String, pageable: Pageable): Page<Photographer>? {
       return photographerRepo.findByStatus(status, searchText, pageable)
    }

    fun findByPlaceId(placeId: Long, pageable: Pageable): Page<Photographer>? {
       return photographerRepo.findByPlaceId(placeId, pageable)
    }

    fun findNearBy(latitude: Double, longitude: Double, searchText: String, pageable: Pageable): Page<Photographer>? {
       return photographerRepo.findNearbyPhotographers(latitude, longitude, searchText, pageable)
    }

}