package com.local.guider.services

import com.local.guider.entities.Image
import com.local.guider.entities.User
import com.local.guider.repositories.ImageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ImageService(
    private val imageRepo: ImageRepository
) {
    fun existsById(id: Long): Boolean {
        return imageRepo.existsById(id)
    }

    fun deleteById(id: Long) {
        imageRepo.deleteById(id)
    }

    fun save(image: Image) {
        imageRepo.save(image)
    }

    fun findByPage(pageable: Pageable): Page<Image>? {
        return imageRepo.findAll(pageable)
    }

    fun findByPhotographerId(id: Long, pageable: Pageable): Page<Image>? {
        return imageRepo.findByPhotographerId(id, pageable)
    }

    fun findByGuiderId(id: Long, pageable: Pageable): Page<Image>? {
        return imageRepo.findByGuiderId(id, pageable)
    }

    fun findByPlaceId(id: Long, pageable: Pageable): Page<Image>? {
        return imageRepo.findByPlaceId(id, pageable)
    }

}