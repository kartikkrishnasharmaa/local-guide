package com.local.guider.controller

import ExcelExporter
import com.local.guider.entities.*
import com.local.guider.models.BaseResponse
import com.local.guider.models.download_models.*
import com.local.guider.network_utils.Endpoints
import com.local.guider.services.*
import com.local.guider.utils.TimeUtils
import com.local.guider.utils.Utils
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayOutputStream
import java.io.IOException

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class DownloadController(
    private val userService: UserService,
    private val photographerService: PhotographerService,
    private val guiderService: GuiderService,
    private val servicesService: ServicesService,
    private val placeService: PlaceService,
    private val transactionService: TransactionService,
    private val withdrawalService: WithdrawalService,
    private val appointmentService: AppointmentService
) {

    @RequestMapping(Endpoints.DOWNLOAD_USERS, method = [RequestMethod.GET, RequestMethod.POST])
    fun downloadUsers(): BaseResponse<ByteArray> {
        val users: List<User> = userService.findAll()
        val excelExporter = ExcelExporter()
        return try {
            val outputStream = ByteArrayOutputStream()
            val downloadUsers = ArrayList<UserDownloadModel>()
            users.forEach { user ->
                val userDownloadModel = UserDownloadModel()
                userDownloadModel.user_Id = user.id
                userDownloadModel.name = user.name
                userDownloadModel.email = user.email
                userDownloadModel.phone = user.phone
                userDownloadModel.photographer = if (user.photographer == true) "Yes" else "No"
                userDownloadModel.guider = if (user.guider == true) "Yes" else "No"
                userDownloadModel.address = user.address
                userDownloadModel.gender = user.gender
                userDownloadModel.date_Of_Birth = user.dob
                userDownloadModel.username = user.username
                userDownloadModel.profile_Image_Url = if (user.profile != null) Utils.domain + user.profile else "NA"
                userDownloadModel.current_Balance = user.balance
                userDownloadModel.total_Appointments = appointmentService.countByUserId(user.id)
                userDownloadModel.joined_On = TimeUtils.formatDate(user.createdOn!!, format = TimeUtils.FORMAT_yyyy_MM_dd_hh_mm_a)
                downloadUsers.add(userDownloadModel)
            }
            excelExporter.exportToExcel("Visitors",downloadUsers, outputStream)
            val excelContent = outputStream.toByteArray()

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_OCTET_STREAM
            headers.setContentDispositionFormData("Visitors", "visitors.xlsx")

            return BaseResponse<ByteArray>().success(data = excelContent)
        } catch (e: IOException) {
            e.printStackTrace()
            BaseResponse<ByteArray>().failed(message = e.message ?: "Something went wrong")
        }
    }

    @RequestMapping(Endpoints.DOWNLOAD_PHOTOGRAPHERS, method = [RequestMethod.GET, RequestMethod.POST])
    fun downloadPhotographers(
    ): BaseResponse<ByteArray> {
        val users: List<Photographer> = photographerService.findAll()
        val excelExporter = ExcelExporter()
        return try {
            val outputStream = ByteArrayOutputStream()
            val downloadUsers = ArrayList<PhotographerDownloadModel>()
            users.forEach { photographer ->

                if (photographer.userId != null) {
                    val user = userService.findById(photographer.userId!!)

                    val photographerDownloadModel = PhotographerDownloadModel()
                    photographerDownloadModel.photographer_Id = photographer.id
                    photographerDownloadModel.owner_Id = photographer.id
                    photographerDownloadModel.owner_Name = user?.name
                    photographerDownloadModel.owner_Date_Of_Birth = user?.dob
                    photographerDownloadModel.owner_Profile_Image_Url = if (user?.profile != null) Utils.domain + user.profile else "NA"
                    photographerDownloadModel.owner_Username = user?.username

                    photographerDownloadModel.firm_Name = photographer.firmName
                    photographerDownloadModel.email = photographer.email
                    photographerDownloadModel.phone = photographer.phone
                    photographerDownloadModel.address = photographer.address
                    photographerDownloadModel.rating = photographer.rating

                    photographerDownloadModel.approval_Status = photographer.approvalStatus

                    photographerDownloadModel.featured_Image_Url = if (photographer.featuredImage != null) Utils.domain + photographer.featuredImage else "NA"
                    photographerDownloadModel.place_Name = photographer.placeName
                    photographerDownloadModel.current_Balance = photographer.balance
                    photographerDownloadModel.total_Appointments = appointmentService.countByPhotographerId(photographerId = photographer.id)

                    var services = ""
                    servicesService.getByPhotographerId(photographer.id)?.forEach {
                        services += "Service: ${it.title}, Price: ${it.servicePrice} \n"
                    }

                    photographerDownloadModel.services = services
                    photographerDownloadModel.id_Proof_Type = photographer.idProofType
                    photographerDownloadModel.id_Proof_Front = if (photographer.idProofFront != null) Utils.domain + photographer.idProofFront else "NA"
                    photographerDownloadModel.id_Proof_Back = if (photographer.idProofBack != null) Utils.domain + photographer.idProofBack else "NA"

                    photographerDownloadModel.joined_On = TimeUtils.formatDate(photographer.createdOn!!, format = TimeUtils.FORMAT_yyyy_MM_dd_hh_mm_a)
                    downloadUsers.add(photographerDownloadModel)
                }
            }
            if (downloadUsers.isEmpty()) {
                return BaseResponse<ByteArray>().failed("No Photographers Found")
            }
            excelExporter.exportToExcel("Photographers",downloadUsers, outputStream)
            val excelContent = outputStream.toByteArray()

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_OCTET_STREAM
            headers.setContentDispositionFormData("Photographers", "photographers.xlsx")

            return BaseResponse<ByteArray>().success(data = excelContent)
        } catch (e: IOException) {
            e.printStackTrace()
            BaseResponse<ByteArray>().failed(message = e.message ?: "Something went wrong")
        }
    }

    @RequestMapping(Endpoints.DOWNLOAD_GUIDERS, method = [RequestMethod.GET, RequestMethod.POST])
    fun downloadGuiders(
    ): BaseResponse<ByteArray> {
        val users: List<Guider> = guiderService.findAll()
        val excelExporter = ExcelExporter()
        return try {
            val outputStream = ByteArrayOutputStream()
            val downloadUsers = ArrayList<PhotographerDownloadModel>()
            users.forEach { guider ->

                if (guider.userId != null) {
                    val user = userService.findById(guider.userId!!)

                    val guiderDownloadModel = PhotographerDownloadModel()
                    guiderDownloadModel.photographer_Id = guider.id
                    guiderDownloadModel.owner_Id = guider.id
                    guiderDownloadModel.owner_Name = user?.name
                    guiderDownloadModel.owner_Date_Of_Birth = user?.dob
                    guiderDownloadModel.owner_Profile_Image_Url = if (user?.profile != null) Utils.domain + user.profile else "NA"
                    guiderDownloadModel.owner_Username = user?.username

                    guiderDownloadModel.firm_Name = guider.firmName
                    guiderDownloadModel.email = guider.email
                    guiderDownloadModel.phone = guider.phone
                    guiderDownloadModel.address = guider.address
                    guiderDownloadModel.rating = guider.rating


                    guiderDownloadModel.featured_Image_Url = if (guider.featuredImage != null) Utils.domain + guider.featuredImage else "NA"
                    guiderDownloadModel.place_Name = guider.placeName
                    guiderDownloadModel.current_Balance = guider.balance
                    guiderDownloadModel.total_Appointments = appointmentService.countByGuiderId(guiderId = guider.id)

                    guiderDownloadModel.approval_Status = guider.approvalStatus

                    var services = ""
                    servicesService.getByGuiderId(guider.id)?.forEach {
                        services += "Service: ${it.title}, Price: ${it.servicePrice} \n"
                    }

                    guiderDownloadModel.services = services
                    guiderDownloadModel.id_Proof_Type = guider.idProofType
                    guiderDownloadModel.id_Proof_Front = if (guider.idProofFront != null) Utils.domain + guider.idProofFront else "NA"
                    guiderDownloadModel.id_Proof_Back = if (guider.idProofBack != null) Utils.domain + guider.idProofBack else "NA"

                    guiderDownloadModel.joined_On = TimeUtils.formatDate(guider.createdOn!!, format = TimeUtils.FORMAT_yyyy_MM_dd_hh_mm_a)
                    downloadUsers.add(guiderDownloadModel)
                }
            }
            if (downloadUsers.isEmpty()) {
                return BaseResponse<ByteArray>().failed("No Guider Found")
            }
            excelExporter.exportToExcel("Guiders",downloadUsers, outputStream)
            val excelContent = outputStream.toByteArray()

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_OCTET_STREAM
            headers.setContentDispositionFormData("Guiders", "guiders.xlsx")

            return BaseResponse<ByteArray>().success(data = excelContent)
        } catch (e: IOException) {
            e.printStackTrace()
            BaseResponse<ByteArray>().failed(message = e.message ?: "Something went wrong")
        }
    }

    @RequestMapping(Endpoints.DOWNLOAD_PLACES, method = [RequestMethod.GET, RequestMethod.POST])
    fun downloadPlaces(): BaseResponse<ByteArray> {
        val places: List<Place> = placeService.findAll()
        val excelExporter = ExcelExporter()
        return try {
            val outputStream = ByteArrayOutputStream()
            val downloadPlaces = ArrayList<PlaceDownloadModel>()
            places.forEach { place ->
                val placeDownloadModel = PlaceDownloadModel()
                placeDownloadModel.place_Id = place.id
                placeDownloadModel.place_Name = place.placeName
                placeDownloadModel.featured_Image = Utils.domain + place.featuredImage
                placeDownloadModel.state = place.state
                placeDownloadModel.city = place.city
                placeDownloadModel.full_Address = place.fullAddress

                placeDownloadModel.latitude = place.latitude
                placeDownloadModel.longitude = place.longitude
                placeDownloadModel.rating = place.rating
                placeDownloadModel.description = place.description
                placeDownloadModel.created_On = TimeUtils.formatDate(place.createdOn!!, format = TimeUtils.FORMAT_yyyy_MM_dd_hh_mm_a)
                downloadPlaces.add(placeDownloadModel)
            }
            excelExporter.exportToExcel("Places",downloadPlaces, outputStream)
            val excelContent = outputStream.toByteArray()

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_OCTET_STREAM
            headers.setContentDispositionFormData("Places", "places.xlsx")

            return BaseResponse<ByteArray>().success(data = excelContent)
        } catch (e: IOException) {
            e.printStackTrace()
            BaseResponse<ByteArray>().failed(message = e.message ?: "Something went wrong")
        }
    }

     @RequestMapping(Endpoints.DOWNLOAD_TRANSACTIONS, method = [RequestMethod.GET, RequestMethod.POST])
    fun downloadTransactions(): BaseResponse<ByteArray> {
        val places: List<Transaction>? = transactionService.getAllWalletTransactions()
        val excelExporter = ExcelExporter()
        return try {
            val outputStream = ByteArrayOutputStream()
            val downloadPlaces = ArrayList<TransactionDownloadModel>()
            places?.forEach { transaction ->

                val transactionDownloadModel = TransactionDownloadModel()
                transactionDownloadModel.transaction_Id = transaction.id
                if (transaction.userId != null) {
                    val user = userService.findById(transaction.userId!!)
                    if (user != null) {
                        transactionDownloadModel.applicant_Id = user.id
                        transactionDownloadModel.applicant_Name = user.name
                        transactionDownloadModel.applicant_Role = "Visitor"
                    }
                } else if (transaction.photographerId != null) {
                    val photographer = photographerService.findById(transaction.photographerId!!)
                    if (photographer != null) {
                        transactionDownloadModel.applicant_Id = photographer.id
                        transactionDownloadModel.applicant_Name = photographer.firmName
                        transactionDownloadModel.applicant_Role = "Photographer"
                    }
                }  else if (transaction.guiderId != null) {
                    val guider = guiderService.findById(transaction.guiderId!!)
                    if (guider != null) {
                        transactionDownloadModel.applicant_Id = guider.id
                        transactionDownloadModel.applicant_Name = guider.firmName
                        transactionDownloadModel.applicant_Role = "Guider"
                    }
                }

                transactionDownloadModel.amount = transaction.amount
                transactionDownloadModel.payment_Status = transaction.paymentStatus
                transactionDownloadModel.payment_Token = transaction.paymentToken
                transactionDownloadModel.created_On = TimeUtils.formatDate(transaction.createdOn!!, format = TimeUtils.FORMAT_yyyy_MM_dd_hh_mm_a)
                downloadPlaces.add(transactionDownloadModel)
            }
            excelExporter.exportToExcel("Transactions",downloadPlaces, outputStream)
            val excelContent = outputStream.toByteArray()

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_OCTET_STREAM
            headers.setContentDispositionFormData("Transactions", "transactions.xlsx")

            return BaseResponse<ByteArray>().success(data = excelContent)
        } catch (e: IOException) {
            e.printStackTrace()
            BaseResponse<ByteArray>().failed(message = e.message ?: "Something went wrong")
        }
    }


    @RequestMapping(Endpoints.DOWNLOAD_WITHDRAWALS, method = [RequestMethod.GET, RequestMethod.POST])
    fun downloadWithdrawals(): BaseResponse<ByteArray> {
        val withdrawals: List<Withdrawal> = withdrawalService.getAll()
        val excelExporter = ExcelExporter()
        return try {
            val outputStream = ByteArrayOutputStream()
            val downloadWithdrawals = ArrayList<WithdrawalDownloadModel>()
            withdrawals.forEach { withdrawal ->
                val transactionDownloadModel = WithdrawalDownloadModel()
                transactionDownloadModel.withdrawal_Id = withdrawal.id

                if (withdrawal.photographerId != null) {
                    val photographer = photographerService.findById(withdrawal.photographerId!!)
                    transactionDownloadModel.applicant_Id = photographer?.id
                    transactionDownloadModel.applicant_Name = photographer?.firmName
                    transactionDownloadModel.applicant_Role = "Photographer"
                } else if (withdrawal.guiderId != null) {
                    val guider = guiderService.findById(withdrawal.guiderId!!)
                    transactionDownloadModel.applicant_Id = guider?.id
                    transactionDownloadModel.applicant_Name = guider?.firmName
                    transactionDownloadModel.applicant_Role = "Guider"
                }

                transactionDownloadModel.amount = withdrawal.amount
                transactionDownloadModel.payment_Status = withdrawal.paymentStatus
                transactionDownloadModel.bank_Name = withdrawal.bankName
                transactionDownloadModel.account_Number = withdrawal.accountNumber
                transactionDownloadModel.account_Holder_Name = withdrawal.accountHolderName
                transactionDownloadModel.ifsc = withdrawal.ifsc
                transactionDownloadModel.upiId = withdrawal.upiId

                transactionDownloadModel.created_On = TimeUtils.formatDate(withdrawal.createdOn!!, format = TimeUtils.FORMAT_yyyy_MM_dd_hh_mm_a)
                downloadWithdrawals.add(transactionDownloadModel)
            }
            excelExporter.exportToExcel("Withdrawals",downloadWithdrawals, outputStream)
            val excelContent = outputStream.toByteArray()

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_OCTET_STREAM
            headers.setContentDispositionFormData("Withdrawals", "withdrawals.xlsx")

            return BaseResponse<ByteArray>().success(data = excelContent)
        } catch (e: IOException) {
            e.printStackTrace()
            BaseResponse<ByteArray>().failed(message = e.message ?: "Something went wrong")
        }
    }

}