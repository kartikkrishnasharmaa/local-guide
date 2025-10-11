package com.local.guider.repositories

import com.local.guider.entities.Photographer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PhotographerRepository : JpaRepository<Photographer, Long> {

    fun existsByPhone(phone: String): Boolean?
    fun existsByUserId(userId: Long): Boolean?

    @Query(
        "SELECT p FROM Photographer p WHERE p.approvalStatus = 'Approved' " +
                "AND (:searchText IS NULL OR " +
                "LOWER(TRIM(p.firmName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(p.placeName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(p.address)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%'))) " +
                "ORDER BY " +
                "(6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(p.latitude)))), " +
                "COALESCE(p.rating, 0) DESC"
    )
    fun findNearbyPhotographers(
        latitude: Double,
        longitude: Double,
        searchText: String?,
        pageable: Pageable
    ): Page<Photographer>?

    @Query(
        "SELECT p FROM Photographer p WHERE p.approvalStatus = :status " +
                "AND (:searchText IS NULL OR " +
                "LOWER(TRIM(p.firmName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(p.placeName)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(p.address)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%'))) " +
                "ORDER BY COALESCE(p.rating, 0) DESC"
    )
    fun findByStatus(
        status: String,
        searchText: String?,
        pageable: Pageable
    ): Page<Photographer>?

    @Query(
        value = "SELECT * FROM photographer " +
                "WHERE approval_status = 'Approved' " +
                "AND FIND_IN_SET(:placeId, places) > 0",
        countQuery = "SELECT COUNT(*) FROM photographer WHERE approval_status = 'Approved' AND FIND_IN_SET(:placeId, places) > 0",
        nativeQuery = true
    )
    fun findByPlaceId(placeId: Long, pageable: Pageable): Page<Photographer>?

    @Query(
        value = "SELECT * FROM photographer " +
                "WHERE approval_status = 'Approved' " +
                "ORDER BY COALESCE(rating, 0) DESC",
        countQuery = "SELECT COUNT(*) FROM photographer WHERE approval_status = 'Approved'",
        nativeQuery = true
    )
    fun findApproved(pageable: Pageable): Page<Photographer>?

//    @Modifying
//    @Transactional
//    @Query(
//        value = "DELETE FROM photographer WHERE id = :id",
//        nativeQuery = true
//    )
//    fun deleteByKey(id: Long)

}