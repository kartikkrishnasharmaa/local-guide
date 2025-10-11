package com.local.guider.repositories

import com.local.guider.entities.Guider
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface GuiderRepository: JpaRepository<Guider, Long> {

    fun existsByPhone(phone: String): Boolean?
    fun existsByUserId(userId: Long): Boolean?

    @Query(
        "SELECT g FROM Guider g WHERE g.approvalStatus = 'Approved' " +
                "AND (:searchText IS NULL OR " +
                "LOWER(TRIM(g.firmName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(g.placeName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(g.address)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%'))) " +
                "ORDER BY " +
                "(6371 * acos(cos(radians(:latitude)) * cos(radians(g.latitude)) * cos(radians(g.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(g.latitude)))), " +
                "COALESCE(g.rating, 0) DESC",
        countQuery = "SELECT g FROM Guider g WHERE g.approvalStatus = 'Approved' " +
                "AND (:searchText IS NULL OR " +
                "LOWER(TRIM(g.firmName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(g.placeName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(g.address)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%'))) " +
                "ORDER BY " +
                "(6371 * acos(cos(radians(:latitude)) * cos(radians(g.latitude)) * cos(radians(g.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(g.latitude)))), " +
                "COALESCE(g.rating, 0) DESC"
    )
    fun findNearbyGuiders(
        latitude: Double,
        longitude: Double,
        searchText: String?,
        pageable: Pageable
    ): Page<Guider>?

    @Query(
        "SELECT g FROM Guider g WHERE g.approvalStatus = :status " +
                "AND (:searchText IS NULL OR " +
                "LOWER(TRIM(g.firmName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(g.placeName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(g.address)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%'))) " +
                "ORDER BY COALESCE(g.rating, 0) DESC",
        countQuery = "SELECT g FROM Guider g WHERE g.approvalStatus = :status " +
                "AND (:searchText IS NULL OR " +
                "LOWER(TRIM(g.firmName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(g.placeName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(g.address)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%'))) " +
                "ORDER BY COALESCE(g.rating, 0) DESC"
    )
    fun findByStatus(
        status: String,
        searchText: String?,
        pageable: Pageable
    ): Page<Guider>?

    @Query(
        value = "SELECT * FROM guider " +
                "WHERE approval_status = 'Approved' " +
                "AND FIND_IN_SET(:placeId, places) > 0 " +
                "ORDER BY COALESCE(rating, 0) DESC",
        countQuery = "SELECT COUNT(*) FROM guider WHERE approval_status = 'Approved' AND FIND_IN_SET(:placeId, places) > 0",
        nativeQuery = true
    )
    fun findByPlaceIds(placeId: Long, pageable: Pageable): Page<Guider>?

    @Query(
        value = "SELECT * FROM guider " +
                "WHERE approval_status = 'Approved' " +
                "ORDER BY COALESCE(rating, 0) DESC",
        countQuery = "SELECT COUNT(*) FROM guider WHERE approval_status = 'Approved'",
        nativeQuery = true
    )
    fun findApproved(pageable: Pageable): Page<Guider>?

}