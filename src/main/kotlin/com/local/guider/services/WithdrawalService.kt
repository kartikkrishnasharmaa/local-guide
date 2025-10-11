package com.local.guider.services

import com.local.guider.entities.Withdrawal
import com.local.guider.repositories.WithdrawalRepository
import org.springframework.data.domain.Example
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class WithdrawalService(
    val withdrawalRepository: WithdrawalRepository
) {

    fun findById(id: Long) = withdrawalRepository.findById(id)
    fun count(): Long {
        return withdrawalRepository.count()
    }
    fun save(withdrawal: Withdrawal) = withdrawalRepository.save(withdrawal)

    fun findByGuiderId(id: Long, pageable: Pageable) = withdrawalRepository.findByGuiderId(id, pageable)

    fun findByPhotographerId(id: Long, pageable: Pageable) = withdrawalRepository.findByPhotographerId(id, pageable)

    fun getAll(pageable: Pageable) = withdrawalRepository.findAll(pageable)
    fun getAll() = withdrawalRepository.findAll()

}