package com.local.guider.controller

import com.local.guider.entities.Guider
import com.local.guider.entities.Photographer
import com.local.guider.entities.Withdrawal
import com.local.guider.enumuration.PaymentStatus
import com.local.guider.models.BaseResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.services.GuiderService
import com.local.guider.services.PhotographerService
import com.local.guider.services.UserService
import com.local.guider.services.WithdrawalService
import com.local.guider.utils.TimeUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrElse

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class WithdrawalController(
    private val userService: UserService,
    private val photographerService: PhotographerService,
    private val guiderService: GuiderService,
    private val withdrawalService: WithdrawalService
) {

    @PostMapping(Endpoints.CREATE_WITHDRAWAL)
    fun makeWithdrawal(
            @RequestParam("photographerId") photographerId: Long?,
            @RequestParam("guiderId") guiderId: Long?,
            @RequestParam("amount") amount: Double?,
            @RequestParam("charge") charge: Double?,
            @RequestParam("amountToBeSettled") amountToBeSettled: Double?,
            @RequestParam("paymentToken") paymentToken: String?,
            @RequestParam("bankName") bankName: String?,
            @RequestParam("accountNumber") accountNumber: String?,
            @RequestParam("accountHolderName") accountHolderName: String?,
            @RequestParam("ifsc") ifsc: String?,
            @RequestParam("upiId") upiId: String?
    ): BaseResponse<Withdrawal> {

        if (guiderId == null && photographerId == null) {
            return BaseResponse<Withdrawal>().failed(
                message = "Specify guiderId or photographerId"
            )
        }

        if (amount == null) {
            return BaseResponse<Withdrawal>().failed(
                message = "Specify amount"
            )
        }

        var photographer: Photographer? = null
        var guider: Guider? = null
        var isUpi = false

        if (photographerId != null) {
            photographer = photographerService.findById(photographerId)
                ?: return BaseResponse<Withdrawal>().failed(
                    message = "Photographer not found"
                )
            if ((photographer.balance ?: 0.0) < amount) {
                return BaseResponse<Withdrawal>().failed(
                    message = "Insufficient balance"
                )
            }
        }
        if (guiderId != null) {
            guider = guiderService.findById(guiderId)
                ?: return BaseResponse<Withdrawal>().failed(
                    message = "Guider not found"
                )
            if ((guider.balance ?: 0.0) < amount) {
                return BaseResponse<Withdrawal>().failed(
                    message = "Insufficient balance"
                )
            }
        }

        if (accountNumber.isNullOrEmpty() && upiId.isNullOrEmpty()) {
            return BaseResponse<Withdrawal>().failed(
                message = "Bank Account Details or UPI ID is required."
            )

        }

        if (accountNumber.isNullOrEmpty() && upiId.isNullOrEmpty().not()) {
            isUpi = true
        }

        val withdrawal = Withdrawal()
        withdrawal.amount = amount
        withdrawal.amountToBeSettled = amountToBeSettled
        withdrawal.charge = charge
        withdrawal.photographerId = photographerId
        withdrawal.guiderId = guiderId
        withdrawal.paymentStatus = PaymentStatus.IN_PROGRESS.value
        withdrawal.bankName = bankName
        withdrawal.accountHolderName = accountHolderName
        withdrawal.accountNumber = accountNumber
        withdrawal.ifsc = ifsc
        withdrawal.upiId = upiId
        withdrawal.useUpi = isUpi
        withdrawal.createdOn = TimeUtils.getCurrentDateTime()
        withdrawal.lastUpdate = TimeUtils.getCurrentDateTime()

        if (photographer != null) {
            photographer.balance = (photographer.balance ?: 0.0) - (amount ?: 0.0)
            photographerService.save(photographer)
        }
        if (guider != null) {
            guider.balance = (guider.balance ?: 0.0) - (amount ?: 0.0)
            guiderService.save(guider)
        }

        withdrawalService.save(withdrawal)

        return BaseResponse<Withdrawal>().success(
            data = withdrawal
        )
    }

    @PostMapping(Endpoints.GET_WITHDRAWAL)
    fun getWithdrawal(
        @RequestParam("photographerId") photographerId: Long?,
        @RequestParam("guiderId") guiderId: Long?,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int
    ): BaseResponse<List<Withdrawal>> {

//        if (photographerId == null && guiderId == null) {
//            return BaseResponse<List<Withdrawal>>().failed(
//                message = "Specify photographerId or guiderId"
//            )
//        }

        val pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "createdOn"))

        val withdrawal = ArrayList<Withdrawal>()

        if (photographerId != null) {
            withdrawal.addAll(withdrawalService.findByPhotographerId(photographerId, pageable).toList())
        } else if (guiderId != null) {
            withdrawal.addAll(withdrawalService.findByGuiderId(guiderId, pageable).toList())
        } else {
            withdrawal.addAll(withdrawalService.getAll(pageable).toList())
        }

        return BaseResponse<List<Withdrawal>>().success(
            data = withdrawal
        )
    }

    @PostMapping(Endpoints.RESPOND_WITHDRAWAL)
    fun respondWithdrawal(
        @RequestParam("withdrawalId") withdrawalId: Long?,
        @RequestParam("status") status: String?,
    ): BaseResponse<Withdrawal> {

        if (withdrawalId == null) {
            return BaseResponse<Withdrawal>().failed(
                message = "Specify withdrawalId"
            )
        }

        if (status == null) {
            return BaseResponse<Withdrawal>().failed(
                message = "Specify status"
            )
        }

        if (status !in PaymentStatus.entries.map { it.value }) {
            return BaseResponse<Withdrawal>().failed(
                message = "Invalid status"
            )
        }

        val withdrawal = withdrawalService.findById(withdrawalId).getOrElse {
            return BaseResponse<Withdrawal>().failed(
                message = "Withdrawal not found"
            )
        }

        // Amount will be added back to balance if status is cancelled
        if (status == PaymentStatus.CANCELED.value) {
            val photographer = photographerService.findById(withdrawal.photographerId ?: 0)
            val guider = guiderService.findById(withdrawal.guiderId ?: 0)
            if (photographer != null) {
                photographer.balance = (photographer.balance ?: 0.0) + (withdrawal.amount ?: 0.0)
                photographerService.save(photographer)
            }
            if (guider != null) {
                guider.balance = (guider.balance ?: 0.0) + (withdrawal.amount ?: 0.0)
                guiderService.save(guider)
            }
        }

        withdrawal.paymentStatus = status
        withdrawal.lastUpdate = TimeUtils.getCurrentDateTime()
        withdrawalService.save(withdrawal)

        return BaseResponse<Withdrawal>().success(
            data = withdrawal
        )
    }

}