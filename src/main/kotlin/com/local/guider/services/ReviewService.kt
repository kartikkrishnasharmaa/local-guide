package com.local.guider.services

import com.local.guider.entities.Review
import com.local.guider.repositories.ReviewRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val reviewRepo: ReviewRepository
) {

    fun findByPhotographerId(photographerId: Long, pageable: Pageable) = reviewRepo.findByPhotographerId(photographerId, pageable)
    fun findByGuiderId(guiderId: Long, pageable: Pageable) = reviewRepo.findByGuiderId(guiderId, pageable)

    fun findByPlaceId(placeId: Long, pageable: Pageable) = reviewRepo.findByPlaceId(placeId, pageable)

    fun findByPhotographerId(photographerId: Long, userId: Long) = reviewRepo.findByPhotographerIdAndUserId(photographerId, userId)
    fun findByGuiderId(guiderId: Long, userId: Long) = reviewRepo.findByGuiderIdAndUserId(guiderId, userId)

    fun findByPlaceId(placeId: Long, userId: Long) = reviewRepo.findByPlaceIdAndUserId(placeId, userId)


    fun deleteById(id: Long) = reviewRepo.deleteById(id)

    fun save(review: Review) = reviewRepo.save(review)



}