package com.local.guider.repositories

import com.local.guider.entities.Review
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<Review, Long>  {
    fun findByPhotographerId(photographerId: Long, pageable: Pageable): Page<Review>?

    fun findByGuiderId(guiderId: Long, pageable: Pageable): Page<Review>?

    fun findByPlaceId(placeId: Long, pageable: Pageable): Page<Review>?
    fun findByPhotographerIdAndUserId(photographerId: Long, userId: Long): List<Review>?

    fun findByGuiderIdAndUserId(guiderId: Long, userId: Long): List<Review>?

    fun findByPlaceIdAndUserId(placeId: Long, userId: Long): List<Review>?

}