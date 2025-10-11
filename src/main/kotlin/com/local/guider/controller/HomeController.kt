package com.local.guider.controller

import com.local.guider.models.BaseResponse
import com.local.guider.models.response.AdminDashboard
import com.local.guider.models.response.HomeResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.services.*
import com.local.guider.utils.Utils.defaultLatLng
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class HomeController(
    private val userService: UserService,
    private val placeService: PlaceService,
    private val guiderService: GuiderService,
    private val photographerService: PhotographerService,
    private val withdrawalService: WithdrawalService,
    private val transactionsService: TransactionService,
) {

    @PostMapping(Endpoints.GET_HOME)
    fun home(@RequestParam("userId") userId: Long?): BaseResponse<HomeResponse?>? {
        if (userId == null) return BaseResponse<HomeResponse?>().failed(
            message = "User id required"
        )
        val user = userService.findById(userId)
            ?: return BaseResponse<HomeResponse?>().failed(
                message = "User not found"
            )
        val pageable = PageRequest.of(0, 10)
        val lat = user.latitude ?: defaultLatLng.lat
        val lng = user.longitude ?: defaultLatLng.lng

        val places =
            placeService.findNearBy(lat, lng, searchText = "", pageable)

        val pageableWithSort = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "rating"))
        val guiders =
            guiderService.findApproved(pageableWithSort)

        val photographers =
            photographerService.findApproved(pageableWithSort)

        val home = HomeResponse()
        home.places = places?.toList()
        home.guiders = guiders?.toList()
        home.photographers = photographers?.toList()
        home.privacyPolicy = "This is app privacy policy for test"
        home.termsAndConditions = "This is app terms and conditions for test"
        home.contactUs = "This is app contact us for test"
        home.aboutUs = "This is app about us for test"

        return BaseResponse<HomeResponse?>().success(
            message = "",
            data = home
        )
    }

    @GetMapping(Endpoints.ADMIN_DASHBOARD)
    fun adminDashboardData(): BaseResponse<AdminDashboard?>? {
        val dashboard = AdminDashboard()
        dashboard.totalUsers = userService.count()
        dashboard.totalPhotographers = photographerService.count()
        dashboard.totalGuiders = guiderService.count()
        dashboard.totalPlaces = placeService.count()
        dashboard.pendingWithdrawals = withdrawalService.count()
        dashboard.totalTransactions = transactionsService.countAllWalletTransactions()
        return BaseResponse<AdminDashboard?>().success(
            data = dashboard,
            message = "User id required"
        )
    }
}