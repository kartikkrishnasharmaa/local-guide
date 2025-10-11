package com.local.guider.services

import com.local.guider.entities.Place
import com.local.guider.repositories.PlaceRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PlaceService(
    private val placeRepo: PlaceRepository,
) {

    fun existByPlaceName(name: String): Boolean {
        return placeRepo.existsByPlaceName(name) ?: false
    }

    fun findAll(): List<Place> {
        return placeRepo.findAll()
    }

    fun count(): Long {
        return placeRepo.count()
    }

    fun existById(id: Long): Boolean {
        return placeRepo.existsById(id)
    }

    fun findById(id: Long): Place? {
        return placeRepo.findByIdOrNull(id)
    }

    fun findNearBy(latitude: Double, longitude: Double, searchText: String, pageable: Pageable): Page<Place>? {
        return placeRepo.findNearbyPlaces(latitude = latitude, longitude = longitude, searchText = searchText, pageable)
    }

    fun findPage(pageable: Pageable): Page<Place> {
        return placeRepo.findAll(pageable)
    }

    fun findTopPlace(pageable: Pageable): Page<Place>? {
        return placeRepo.findTopPlaces(pageable)
    }

    fun findBySearchText(searchText: String, pageable: Pageable): Page<Place>? {
        return placeRepo.findByStatus(searchText, pageable)
    }

    fun deleteById(id: Long) {
        return placeRepo.deleteById(id)
    }

    fun save(place: Place) {
        placeRepo.save(place)
    }
}