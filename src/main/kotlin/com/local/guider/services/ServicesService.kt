package com.local.guider.services

import com.local.guider.repositories.ServiceRepository
import org.springframework.stereotype.Service

@Service
class ServicesService(
    private val serviceRepo: ServiceRepository
) {

    fun getById(id: Long) = serviceRepo.findById(id)

    fun save(service: com.local.guider.entities.Service) = serviceRepo.save(service)

    fun deleteById(id: Long) = serviceRepo.deleteByKey(id)

    fun getByPhotographerId(id: Long) = serviceRepo.findByPhotographerId(id)

    fun getByGuiderId(id: Long) = serviceRepo.findByGuiderId(id)

}