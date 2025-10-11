package com.local.guider.repositories

import com.local.guider.entities.Withdrawal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface WithdrawalRepository: JpaRepository<Withdrawal, Long> {

    fun findByGuiderId(id: Long, pageable: Pageable): Page<Withdrawal>

    fun findByPhotographerId(id: Long, pageable: Pageable): Page<Withdrawal>

}