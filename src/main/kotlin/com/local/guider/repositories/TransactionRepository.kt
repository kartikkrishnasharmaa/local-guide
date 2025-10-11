package com.local.guider.repositories

import com.local.guider.entities.Transaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TransactionRepository: JpaRepository<Transaction, Long> {

    fun findByUserId(userId: Long, pageable: Pageable): Page<Transaction>?
    fun findByPaymentToken(paymentToken: String): Transaction?

    fun findByPhotographerId(photographerId: Long, pageable: Pageable): Page<Transaction>?
    fun findByGuiderId(guiderId: Long, pageable: Pageable): Page<Transaction>?

    @Query(
        "SELECT t FROM Transaction t WHERE t.paymentFor = 'wallet'"
    )
    fun getAllWalletTransactions(pageable: Pageable): Page<Transaction>?

    @Query(
        "SELECT t FROM Transaction t WHERE t.paymentFor = 'wallet'"
    )
    fun getAllWalletTransactions(): List<Transaction>?


    @Query(
        "SELECT COUNT(t) FROM Transaction t WHERE t.paymentFor = 'wallet'"
    )
    fun countAllWalletTransactions(): Long

}