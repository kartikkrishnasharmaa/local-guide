package com.local.guider.repositories

import com.local.guider.entities.Service
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ServiceRepository: JpaRepository<Service, Long> {
    fun findByPhotographerId(photographerId: Long): List<Service>?
    fun findByGuiderId(guiderId: Long): List<Service>?

    @Query(
        "select s from Service s where s.id = :id"
    )
    fun deleteByKey(id: Long)

}