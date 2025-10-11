package com.local.guider.controller

import com.local.guider.dto.CreateRazorpayOrder
import com.local.guider.entities.Transaction
import com.local.guider.entities.User
import com.local.guider.enumuration.PaymentStatus
import com.local.guider.models.BaseResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.services.*
import com.local.guider.utils.TimeUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class TransactionController(
    private val userService: UserService,
    private val photographerService: PhotographerService,
    private val guiderService: GuiderService,
    private val transactionService: TransactionService,
) {

    @PostMapping(Endpoints.CREATE_TRANSACTION)
    fun createTransaction(
        @RequestParam("userId") userId: Long?,
        @RequestParam("photographerId") photographerId: Long?,
        @RequestParam("guiderId") guiderId: Long?,
        @RequestParam("amount") amount: Double?,
        @RequestParam("paymentToken") paymentToken: String?,
    ): BaseResponse<Transaction> {

        if (userId == null && photographerId == null && guiderId == null) {
            return BaseResponse<Transaction>().failed(
                message = "Specify userid of guiderId or photographerId"
            )
        }
        if (userId != null) {
            val user = userService.findById(userId)
                ?: return BaseResponse<Transaction>().failed(
                    message = "User not found"
                )
        }
        if (photographerId != null) {
            val photographer = photographerService.findById(photographerId)
                ?: return BaseResponse<Transaction>().failed(
                    message = "Photographer not found"
                )
        }
        if (guiderId != null) {
            val guider = guiderService.findById(guiderId)
                ?: return BaseResponse<Transaction>().failed(
                    message = "Guider not found"
                )
        }

        val receiptId = "receipt_${System.currentTimeMillis()}"

        val razorpayOrder = CreateRazorpayOrder()
        razorpayOrder.amount = amount?.times(100)?.toInt()
        razorpayOrder.currency = "INR"
        razorpayOrder.receipt = receiptId
        val order = transactionService.createOrder(razorpayOrder)

        val transaction = Transaction()
        transaction.userId = userId
        transaction.photographerId = photographerId
        transaction.guiderId = guiderId
        transaction.amount = amount
        transaction.paymentFor = "wallet"
        transaction.paymentToken = order.id
        transaction.transactionId = receiptId
        transaction.createdOn = TimeUtils.getCurrentDateTime()
        transaction.lastUpdate = TimeUtils.getCurrentDateTime()

        transactionService.save(transaction)

        return BaseResponse<Transaction>().success(
            message = "",
            data = transaction
        )
    }

    @PostMapping(Endpoints.UPDATE_TRANSACTION)
    fun updateTransactions(
        @RequestParam("paymentToken") paymentToken: String?,
        @RequestParam("paymentStatus") paymentStatus: String?,
    ): BaseResponse<Transaction> {

        if (paymentToken == null) {
            return BaseResponse<Transaction>().failed(
                message = "Payment Token is required"
            )
        }
        if (paymentStatus == null) {
            return BaseResponse<Transaction>().failed(
                message = "Payment status is required"
            )
        }

        if (paymentStatus !in PaymentStatus.entries.map { it.value }) {
            return BaseResponse<Transaction>().failed(
                message = "Invalid payment status"
            )
        }

        val transaction = transactionService.findByPaymentToken(paymentToken)
            ?: return BaseResponse<Transaction>().failed(
                message = "Transaction not found"
            )

        transaction.paymentStatus = paymentStatus
        transaction.lastUpdate = TimeUtils.getCurrentDateTime()

        // Balance Update
        if (paymentStatus == PaymentStatus.SUCCESS.value) {
            if (transaction.userId != null) {
                val user = userService.findById(transaction.userId!!)
                    ?: return BaseResponse<Transaction>().failed(
                        message = "User not found"
                    )
                user.balance = (user.balance ?: 0.0) + transaction.amount!!
                userService.save(user)
            }
            if (transaction.photographerId != null) {
                val photographer = photographerService.findById(transaction.photographerId!!)
                    ?: return BaseResponse<Transaction>().failed(
                        message = "Photographer not found"
                    )
                photographer.balance = (photographer.balance ?: 0.0) + transaction.amount!!
                photographerService.save(photographer)
            }
            if (transaction.guiderId != null) {
                val guider = guiderService.findById(transaction.guiderId!!)
                    ?: return BaseResponse<Transaction>().failed(
                        message = "Guider not found"
                    )
                guider.balance = (guider.balance ?: 0.0) + transaction.amount!!
                guiderService.save(guider)
            }
        }

        transaction.lastUpdate = TimeUtils.getCurrentDateTime()
        transactionService.save(transaction)

        return BaseResponse<Transaction>().success(
            message = "",
            data = transaction
        )
    }

    @PostMapping(Endpoints.GET_TRANSACTION)
    fun transactionsList(
        @RequestParam("userId") userId: Long?,
        @RequestParam("photographerId") photographerId: Long?,
        @RequestParam("guiderId") guiderId: Long?,
        @RequestParam("admin") admin: Boolean?,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int
    ): BaseResponse<List<Transaction>> {

        if (userId == null && photographerId == null && guiderId == null && admin != true) {
            return BaseResponse<List<Transaction>>().failed(
                message = "Specify userid of guiderId or photographerId"
            )
        }

        val pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "createdOn"))

        var result = ArrayList<Transaction>()

        if (userId != null) {
            val user = userService.findById(userId)
                ?: return BaseResponse<List<Transaction>>().failed(
                    message = "User not found"
                )
            result = transactionService.findByUserId(userId, pageable)?.let { ArrayList(it) } ?: ArrayList()
            result.forEach {
                it.isCredit = it.paymentFor != "appointment"
            }
        }
        if (photographerId != null) {
            val photographer = photographerService.findById(photographerId)
                ?: return BaseResponse<List<Transaction>>().failed(
                    message = "Photographer not found"
                )
            result =
                transactionService.findByPhotographerId(photographerId, pageable)?.let { ArrayList(it) } ?: ArrayList()
            result.forEach {
                it.isCredit = true
            }
        }
        if (guiderId != null) {
            val guider = guiderService.findById(guiderId)
                ?: return BaseResponse<List<Transaction>>().failed(
                    message = "Guider not found"
                )
            result = transactionService.findByGuiderId(guiderId, pageable)?.let { ArrayList(it) } ?: ArrayList()
            result.forEach {
                it.isCredit = true
            }
        }
        if (admin == true) {
            result = transactionService.getAllWalletTransactions(pageable)?.let { ArrayList(it) } ?: ArrayList()
            result.forEach {
                val user = if (it.userId == null) null else userService.findById(it.userId!!)
                it.other = user?.name
            }
        }

        return BaseResponse<List<Transaction>>().success(
            message = "",
            data = result
        )
    }

    @PostMapping(Endpoints.DELETE_TRANSACTION)
    fun deleteTransaction(
        @RequestParam("id") id: Long
    ) : BaseResponse<String> {
        transactionService.deleteTransaction(id)
        return BaseResponse<String>().success(data = "Transaction deleted.")
    }

}