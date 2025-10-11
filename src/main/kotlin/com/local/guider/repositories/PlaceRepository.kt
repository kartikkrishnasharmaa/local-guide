package com.local.guider.repositories

import com.local.guider.entities.Place
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PlaceRepository: JpaRepository<Place, Long> {

    fun existsByPlaceName(placeName: String): Boolean?

    @Query("SELECT p FROM Place p " +
            "WHERE (:searchText IS NULL OR LOWER(TRIM(p.placeName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) " +
            "OR LOWER(TRIM(p.state)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) " +
            "OR LOWER(TRIM(p.fullAddress)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%'))) " +
            "ORDER BY " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(p.latitude)))), " +
            "COALESCE(p.views, 0) DESC")
    fun findNearbyPlaces(
        latitude: Double,
        longitude: Double,
        searchText: String?,
        pageable: Pageable
    ): Page<Place>?


    @Query("SELECT p FROM Place p WHERE p.isTop = true")
    fun findTopPlaces(
        pageable: Pageable
    ): Page<Place>?

    @Query(
        "SELECT p FROM Place p WHERE (:searchText IS NULL OR " +
                "LOWER(TRIM(p.placeName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(p.state)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(p.fullAddress)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')))"
    )
    fun findByStatus(searchText: String?, pageable: Pageable): Page<Place>?


}