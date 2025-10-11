package com.local.guider.repositories

import com.local.guider.entities.Image
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ImageRepository: JpaRepository<Image, Long> {
    fun findByPhotographerId(photographerId: Long, pageable: Pageable): Page<Image>?
    fun findByGuiderId(guiderId: Long, pageable: Pageable): Page<Image>?
    fun findByPlaceId(placeId: Long, pageable: Pageable): Page<Image>?
}