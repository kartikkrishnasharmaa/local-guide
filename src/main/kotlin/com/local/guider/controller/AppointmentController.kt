package com.local.guider.controller

import com.google.gson.Gson
import com.local.guider.dto.AppointmentResponse
import com.local.guider.dto.CreateAppointmentDto
import com.local.guider.entities.*
import com.local.guider.enumuration.AppointmentStatus
import com.local.guider.enumuration.PaymentStatus
import com.local.guider.models.BaseResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.services.*
import com.local.guider.utils.TimeUtils
import com.local.guider.utils.mkFirstUppercase
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrElse

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class AppointmentController(
    private val servicesService: ServicesService,
    private val photographerService: PhotographerService,
    private val guiderService: GuiderService,
    private val appointmentService: AppointmentService,
    private val transactionService: TransactionService,
    private val notificationService: NotificationService,
    private val userService: UserService
) {

    @PostMapping(Endpoints.CREATE_APPOINTMENT)
    fun create(@RequestBody payload: CreateAppointmentDto): BaseResponse<Appointment?>? {

        val user = userService.findById(payload.userId)
            ?: return BaseResponse<Appointment?>().failed(
                message = "User not found"
            )

        if (payload.photographerId == null && payload.guiderId == null) {
            return BaseResponse<Appointment?>().failed(
                message = "Photographer or guider id is required"
            )
        }
        if (payload.dateTime == null) {
            return BaseResponse<Appointment?>().failed(
                message = "Date and time is required"
            )
        }

        val dateTime = try {
            TimeUtils.stringToDate(payload.dateTime!!, format = TimeUtils.FORMAT_yyyy_MM_dd_hh_mm_a)
        } catch (e: Exception) {
            return BaseResponse<Appointment?>().failed(
                message = "Date and time is incorrect. ${e.message}"
            )
        }
        if (payload.transactionId == null) {
            return BaseResponse<Appointment?>().failed(
                message = "Transaction id is required"
            )
        }
        if (payload.serviceId == null) {
            return BaseResponse<Appointment?>().failed(
                message = "Service id is required"
            )
        }

        val service = servicesService.getById(payload.serviceId!!).getOrElse {
            return BaseResponse<Appointment?>().failed(
                message = "Service not found"
            )
        }

        if (payload.appointmentCharge == null) {
            return BaseResponse<Appointment?>().failed(
                message = "Appointment charge is required"
            )
        }

        if (payload.serviceCost == null) {
            return BaseResponse<Appointment?>().failed(
                message = "Service cost is required"
            )
        }

        if (payload.totalPayment == null) {
            return BaseResponse<Appointment?>().failed(
                message = "Total payment is required"
            )
        }

        if ((user.balance ?: 0.0) < (payload.totalPayment ?: 0.0)) {
            return BaseResponse<Appointment?>().failed(
                message = "Insufficient balance"
            )
        }

        val appointment = Appointment()

        appointment.userId = payload.userId
        appointment.photographerId = payload.photographerId
        appointment.guiderId = payload.guiderId
        appointment.date = dateTime
        appointment.serviceName = service.title
        appointment.serviceImage = service.image
        appointment.appointmentCharge = payload.appointmentCharge
        appointment.serviceCost = payload.serviceCost
        appointment.totalPayment = payload.totalPayment
        appointment.note = payload.note
        appointment.transactionId = payload.transactionId
        appointment.paymentStatus = PaymentStatus.SUCCESS.value.lowercase()
        appointment.appointmentStatus = AppointmentStatus.REQUESTED.key

        appointment.createdOn = TimeUtils.getCurrentDateTime()
        appointment.lastUpdate = TimeUtils.getCurrentDateTime()

        user.balance = (user.balance ?: 0.0) - (payload.totalPayment ?: 0.0)
        user.lastUpdate = TimeUtils.getCurrentDateTime()

        val newTransaction = Transaction()
        newTransaction.userId = payload.userId
        newTransaction.photographerId = payload.photographerId
        newTransaction.guiderId = payload.guiderId
        newTransaction.amount = payload.serviceCost
        newTransaction.charge = payload.appointmentCharge
        newTransaction.totalAmount = payload.totalPayment
        newTransaction.paymentStatus = PaymentStatus.SUCCESS.value
        newTransaction.paymentFor = "appointment"
        newTransaction.transactionId = "transaction_${System.currentTimeMillis()}"
        newTransaction.createdOn = TimeUtils.getCurrentDateTime()
        newTransaction.lastUpdate = TimeUtils.getCurrentDateTime()

        val notification = Notification()
        notification.photographerId = payload.photographerId
        notification.guiderId = payload.guiderId
        notification.type = "appointment"
        notification.title = "Appointment Request!✉️"
        notification.description = "You have a new appointment 🗓️ request from ${user.name} ."
        notification.createdOn = TimeUtils.getCurrentDateTime()
        notification.lastUpdate = TimeUtils.getCurrentDateTime()

        try {
            if (payload.photographerId != null) {
                val photographer = photographerService.findById(payload.photographerId!!)
                val firmOwner = userService.findById(photographer?.userId!!)
                notification.sendTo = firmOwner?.fcm
            } else {
                val guider = photographerService.findById(payload.guiderId!!)
                val firmOwner = userService.findById(guider?.userId!!)
                notification.sendTo = firmOwner?.fcm
            }
        } catch (e: Exception) {
         e.printStackTrace()
        }

        notificationService.save(notification)

        transactionService.save(newTransaction)
        appointment.transactionId = newTransaction.id.toString()

        userService.save(user)
        appointmentService.save(appointment)

        newTransaction.appointmentId = appointment.id
        transactionService.save(newTransaction)

        return BaseResponse<Appointment?>().success(
            message = "Appointment created successfully",
            data = appointment
        )
    }

    @PostMapping(Endpoints.GET_APPOINTMENTS)
    fun getAppointment(
        @RequestParam("photographerId") photographerId: Long?,
        @RequestParam("guiderId") guiderId: Long?,
        @RequestParam("userId") userId: Long?,
        @RequestParam("status") status: String?,
        @RequestParam("searchText") searchText: String?,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int
    ): BaseResponse<List<AppointmentResponse>?>? {
        if (photographerId == null && guiderId == null && userId == null) {
            return BaseResponse<List<AppointmentResponse>?>().failed(
                message = "photographer or guider or user id is required"
            )
        }
        var appointmentPage: Page<Appointment>? = null

        val pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "date"))

        if (photographerId != null) {
            appointmentPage = appointmentService.findPageByStatusPhotographerId(
                status ?: AppointmentStatus.COMPLETED.key,
                photographerId,
                searchText ?: "",
                pageable
            )
        } else if (guiderId != null) {
            appointmentPage = appointmentService.findPageByStatusGuiderId(
                status ?: AppointmentStatus.COMPLETED.key,
                guiderId,
                searchText ?: "",
                pageable
            )
        } else if (userId != null) {
            appointmentPage = appointmentService.findPageByUserId(
                userId,
                searchText ?: "",
                pageable
            )
        }

        if (appointmentPage == null) {
            return BaseResponse<List<AppointmentResponse>?>().failed(
                message = "Appointments not found"
            )
        }

        val result = ArrayList<AppointmentResponse>()
        appointmentPage.toList().forEach {
            val item = Gson().fromJson(Gson().toJson(it), AppointmentResponse::class.java)
            val user = userService.findById(it.userId ?: 0)
            item.customerName = user?.name ?: ""
            result.add(item)
        }

        return BaseResponse<List<AppointmentResponse>?>().success(
            message = "Appointments fetched successfully",
            data = result,
            mTotalPage = appointmentPage.totalPages,
            page = page
        )

    }

    @PostMapping(Endpoints.RESPOND_APPOINTMENT)
    fun respondToAppointment(
        @RequestParam("status") status: String?,
        @RequestParam("note") note: String?,
        @RequestParam("appointmentId") appointmentId: Long?,
    ): BaseResponse<Appointment>? {
        if (status == null || appointmentId == null) {
            return BaseResponse<Appointment>().failed(
                message = "Status and appointment id is required"
            )
        }

        if (!appointmentService.existsById(appointmentId)) {
            return BaseResponse<Appointment>().failed(
                message = "Invalid appointment id"
            )
        }

        if (status !in AppointmentStatus.entries.map { it.key }) {
            return BaseResponse<Appointment>().failed(
                message = "Invalid status"
            )
        }

        val appointment = appointmentService.findById(appointmentId)

        appointment.appointmentStatus = status
        appointment.note = note
        appointment.lastUpdate = TimeUtils.getCurrentDateTime()

        appointmentService.save(appointment)

        var firmName = ""

        val user = userService.findById(appointment.userId!!)
            ?: return BaseResponse<Appointment>().failed(
                message = "User not found"
            )

        var photographer: Photographer? = null
        var guider: Guider? = null

        if (status == AppointmentStatus.COMPLETED.key) {

            var firmUser: User? = null

            if (appointment.photographerId != null) {
                photographer = photographerService.findById(appointment.photographerId!!)
                photographer?.balance = (photographer?.balance ?: .0) + (appointment.serviceCost ?: .0)
                photographer?.lastUpdate = TimeUtils.getCurrentDateTime()
                photographerService.save(photographer!!)
                firmName = photographer.firmName ?: ""
                firmUser = if (photographer.userId != null) userService.findById(photographer.userId!!) else null
            } else if (appointment.guiderId != null) {
                guider = guiderService.findById(appointment.guiderId!!)
                guider?.balance = (guider?.balance ?: .0) + (appointment.serviceCost ?: .0)
                guider?.lastUpdate = TimeUtils.getCurrentDateTime()
                guiderService.save(guider!!)
                firmName = guider.firmName ?: ""
                firmUser = if (guider.userId != null) userService.findById(guider.userId!!) else null
            }

            if (firmUser != null) {
                val notification = Notification()
                notification.userId = appointment.userId
                notification.type = "balance_update"
                notification.title = appointment.serviceCost.toString() + "₹ Credited 🎉"
                notification.description = "Hi $firmName, your wallet has been credited with ₹${appointment.serviceCost.toString()} for your appointment with ${user.name}"
                notification.markAsRead = false
                notification.createdOn = TimeUtils.getCurrentDateTime()
                notification.lastUpdate = TimeUtils.getCurrentDateTime()
                notification.sendTo = firmUser.fcm
                notificationService.save(notification)
            }

        } else if (status == AppointmentStatus.CANCELED.key) {
            user.balance = (user.balance ?: .0) + (appointment.serviceCost ?: .0)
            user.lastUpdate = TimeUtils.getCurrentDateTime()
            userService.save(user)
            if (appointment.photographerId != null) {
                photographer = photographerService.findById(appointment.photographerId!!)
                firmName = photographer?.firmName ?: ""
            } else if (appointment.guiderId != null) {
                guider = guiderService.findById(appointment.guiderId!!)
                firmName = guider?.firmName ?: ""
            }
            val notification = Notification()
            notification.userId = appointment.userId
            notification.type = "balance_update"
            notification.title = appointment.serviceCost.toString() + "₹ Credited 💸"
            notification.description = "Hi ${user.name}, ₹${appointment.serviceCost.toString()} refunded because your appointment with $firmName was canceled."
            notification.markAsRead = false
            notification.createdOn = TimeUtils.getCurrentDateTime()
            notification.lastUpdate = TimeUtils.getCurrentDateTime()
            notification.sendTo = user.fcm
            notificationService.save(notification)
        }

        try {
            val notification = Notification()
            notification.userId = appointment.userId
            notification.type = "appointment"
            notification.title = "Appointment ${status.mkFirstUppercase()?.replace("_", " ")}!"
            notification.description = AppointmentStatus.entries.firstOrNull { it.key == status }?.notificationMsg
            notification.markAsRead = false
            notification.createdOn = TimeUtils.getCurrentDateTime()
            notification.lastUpdate = TimeUtils.getCurrentDateTime()
            notification.sendTo = user.fcm
            notificationService.save(notification)

            if (status == AppointmentStatus.COMPLETED.key) {
                val newNotification = Notification()
                newNotification.userId = appointment.userId
                newNotification.type = "appointment"
                newNotification.title = "Leave a review!"
                newNotification.note = if (photographer != null) Gson().toJson(photographer) else Gson().toJson(guider)
                newNotification.description = "Hi ${user.name}, How was your experience at $firmName. Leave a review ⭐️⭐️⭐️⭐️⭐️!"
                newNotification.markAsRead = false
                newNotification.photographerId = photographer?.id
                newNotification.photographerId = guider?.id
                newNotification.createdOn = TimeUtils.getCurrentDateTime()
                newNotification.lastUpdate = TimeUtils.getCurrentDateTime()
                newNotification.sendTo = user.fcm
                notificationService.save(newNotification)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return BaseResponse<Appointment>().success(
            message = "Appointment updated successfully",
            data = appointment
        )
    }

    @PostMapping(Endpoints.GET_APPOINTMENT_BY_TRANSACTION_ID)
    fun getAppointmentByTransactionId(
        @RequestParam("transactionId") transactionId: String,
    ): BaseResponse<Appointment>? {
        val appointment: Appointment = appointmentService.findByTransactionId(transactionId)
            ?: return BaseResponse<Appointment>().failed(message = "Appointment not found")
        return BaseResponse<Appointment>().success(data = appointment, message = "Appointment fetched successfully!")
    }
}