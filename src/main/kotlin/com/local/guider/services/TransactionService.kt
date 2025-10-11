package com.local.guider.services

import com.local.guider.dto.CreateRazorpayOrder
import com.local.guider.entities.Transaction
import com.local.guider.models.CreateOrderResponse
import com.local.guider.repositories.TransactionRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import kotlin.jvm.optionals.getOrElse

@Service
class TransactionService(
    private val transactionRepo: TransactionRepository,
    private val restTemplate: RestTemplate,
    @Value("\${razorpay.api.key}") private val razorpayAPIKey: String,
    @Value("\${razorpay.api.secret_key}") private val razorpaySecretKey: String,
    @Value("\${razorpay.api.base_url}") private val razorpayBaseUrl: String

) {
    fun save(transaction: Transaction) {
        transactionRepo.save(transaction)
    }

    fun createOrder(orderReq: CreateRazorpayOrder):
            CreateOrderResponse {
        val url = razorpayBaseUrl + "orders"

        val headers = HttpHeaders()
        headers.setBasicAuth(razorpayAPIKey, razorpaySecretKey)
        headers.set("Content-Type", "application/json")

        val requestEntity = HttpEntity(orderReq, headers)

        val responseEntity: ResponseEntity<CreateOrderResponse> =
            restTemplate.exchange(url, HttpMethod.POST, requestEntity, CreateOrderResponse::class.java)

        if (responseEntity.statusCode.is2xxSuccessful) {
            return responseEntity.body ?: throw RuntimeException("Failed to parse response body")
        } else {
            throw RuntimeException("Failed to create order. Status code: ${responseEntity.statusCode}")
        }
    }

    fun deleteTransaction(id: Long) {
        transactionRepo.deleteById(id)
    }

    fun findById(id: Long): Transaction? {
        return transactionRepo.findById(id).getOrElse { null }
    }

    fun findByPaymentToken(token: String): Transaction? {
        return transactionRepo.findByPaymentToken(token)
    }

    fun findByUserId(userId: Long, pageable: Pageable): List<Transaction>? {
        return transactionRepo.findByUserId(userId, pageable)?.toList() ?: emptyList()
    }

    fun findByPhotographerId(photographerId: Long, pageable: Pageable): List<Transaction>? {
        return transactionRepo.findByPhotographerId(photographerId, pageable)?.toList() ?: emptyList()
    }

    fun findByGuiderId(guiderId: Long, pageable: Pageable): List<Transaction>? {
        return transactionRepo.findByGuiderId(guiderId, pageable)?.toList() ?: emptyList()
    }

    fun getAllWalletTransactions(pageable: Pageable): List<Transaction>? {
        return transactionRepo.getAllWalletTransactions(pageable)?.toList() ?: emptyList()
    }
    fun getAllWalletTransactions(): List<Transaction>? {
        return transactionRepo.getAllWalletTransactions()?.toList() ?: emptyList()
    }
    fun countAllWalletTransactions(): Long {
        return transactionRepo.countAllWalletTransactions()
    }

}