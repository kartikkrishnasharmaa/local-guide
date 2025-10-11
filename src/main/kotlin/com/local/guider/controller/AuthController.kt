package com.local.guider.controller

import com.local.guider.ApiException
import com.local.guider.dto.*
import com.local.guider.entities.Notification
import com.local.guider.entities.User
import com.local.guider.models.BaseResponse
import com.local.guider.models.response.TokenResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.network_utils.FileUtils
import com.local.guider.services.HashService
import com.local.guider.services.NotificationService
import com.local.guider.services.TokenService
import com.local.guider.services.UserService
import com.local.guider.utils.TimeUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class AuthController(
    private val hashService: HashService,
    private val tokenService: TokenService,
    private val userService: UserService,
    private val notificationService: NotificationService
) {

    @PostMapping(Endpoints.LOGIN)
    fun login(@RequestBody payload: LoginDto): BaseResponse<TokenResponse>? {

        val user = userService.findByPhone(payload.phone)
            ?: return BaseResponse<TokenResponse>().failed(
                message = "Incorrect phone number"
            )

        if (!hashService.checkBcrypt(payload.password, user.password)) {
            return BaseResponse<TokenResponse>().failed(
                message = "Incorrect Password"
            )
        }

        val tokenMap = tokenService.createTokens(user, true)

        return BaseResponse<TokenResponse>().success(
            message = "",
            data = TokenResponse(
                tokenMap.first,
                tokenMap.second,
                user = user
            )
        )
    }

    @PostMapping(Endpoints.REGISTER)
    fun register(@RequestBody payload: RegisterDto): BaseResponse<TokenResponse>? {

        val newUser = User()

        if (payload.name == null) {
            return BaseResponse<TokenResponse>().failed(
                message = "Name is required."
            )
        }

        if (payload.username == null) {
            return BaseResponse<TokenResponse>().failed(
                message = "Username is required."
            )
        }

        if (payload.phone == null) {
            return BaseResponse<TokenResponse>().failed(
                message = "Phone number is required."
            )
        }

        if (payload.countryCode == null) {
            return BaseResponse<TokenResponse>().failed(
                message = "Country code is required."
            )
        }

        if (payload.password == null) {
            return BaseResponse<TokenResponse>().failed(
                message = "Password is required."
            )
        }

        if (userService.existsByUsername(payload.username)) {
            return BaseResponse<TokenResponse>().failed(
                message = "Username is already exist."
            )
        }

        if (userService.existsByPhone(payload.phone)) {
            return BaseResponse<TokenResponse>().failed(
                message = "Phone number is already exist."
            )
        }

        newUser.password = hashService.hashBcrypt(payload.password)
        newUser.name = payload.name
        newUser.phone = payload.phone
        newUser.countryCode = payload.countryCode
        newUser.address = payload.address
        newUser.longitude = payload.longitude
        newUser.latitude = payload.latitude
        newUser.username = payload.username
        newUser.email = payload.email
        newUser.gender = payload.gender
        newUser.dob = payload.dob
        newUser.createdOn = TimeUtils.getCurrentDateTime()
        newUser.lastUpdate = TimeUtils.getCurrentDateTime()

        userService.save(newUser)

        val tokenMap = tokenService.createTokens(newUser, true)

        val notification = Notification()
        notification.type = "new_account"
        notification.title = "New Account Created"
        notification.sendTo = "admin"
        notification.description = "You have a new user registration.\nName: ${payload.name}\nUserId: ${newUser.id}"
        notification.createdOn = TimeUtils.getCurrentDateTime()
        notification.lastUpdate = TimeUtils.getCurrentDateTime()

        notificationService.save(notification)

        return BaseResponse<TokenResponse>().success(
            message = "",
            data = TokenResponse(
                tokenMap.first,
                tokenMap.second,
                user = newUser
            )
        )
    }

    @PostMapping(Endpoints.FORGET_PASSWORD)
    fun forgetPassword(@RequestBody payload: ForgetPasswordDto): BaseResponse<User> {
        if (payload.password == null) {
            return BaseResponse<User>().failed(
                message = "Password is required"
            )
        }
        if (payload.password.length < 6) {
            return BaseResponse<User>().failed(
                message = "Password should contain at least 6 character"
            )
        }
        if (payload.phoneNumber == null) {
            return BaseResponse<User>().failed(
                message = "Phone number is required"
            )
        }
        if (!userService.existsByPhone(payload.phoneNumber)) {
            return BaseResponse<User>().failed(
                message = "No user found with given phone number"
            )
        }
        val user = userService.findByPhone(payload.phoneNumber) ?: throw ApiException(404, "User not found")
        user.password = hashService.hashBcrypt(payload.password)
        user.lastUpdate = TimeUtils.getCurrentDateTime()
        userService.save(user)
        return BaseResponse<User>().success(
            message = "Password reset successful",
            data = user
        )
    }

    @PostMapping(Endpoints.UPDATE_PROFILE)
    fun updateProfile(
        @RequestParam("userId") userId: Long?,
        @RequestParam("name") name: String?,
        @RequestParam("email") email: String?,
        @RequestParam("phone") phone: String?,
        @RequestParam("isBlocked") isBlocked: Boolean?,
        @RequestParam("reasonOfBlock") reasonOfBlock: String?,
        @RequestParam("fcm") fcm: String?,
        @RequestParam("countryCode") countryCode: String?,
        @RequestParam("address") address: String?,
        @RequestParam("latitude") latitude: Double?,
        @RequestParam("longitude") longitude: Double?,
        @RequestParam("gender") gender: String?,
        @RequestParam("dob") dob: String?,
        @RequestParam("profile") profile: MultipartFile?
    ): BaseResponse<User> {
        if (userId == null) {
            return BaseResponse<User>().failed(
                message = "User id is required"
            )
        }
        if (!userService.existsById(userId)) {
            return BaseResponse<User>().failed(
                message = "No user found with given user id"
            )
        }

        val user = userService.findById(userId) ?: throw ApiException(404, "User not found")
        if (!name?.trim().isNullOrEmpty()) user.name = name!!
        if (!email?.trim().isNullOrEmpty()) user.email = email
        if (!phone?.trim().isNullOrEmpty()) user.email = email
        if (!countryCode?.trim().isNullOrEmpty()) user.countryCode = countryCode!!
        if (!address?.trim().isNullOrEmpty()) user.address = address
        if (!longitude?.toString()?.trim().isNullOrEmpty()) user.longitude = longitude
        if (!latitude?.toString()?.trim().isNullOrEmpty()) user.latitude = latitude
        if (!dob?.trim().isNullOrEmpty()) user.dob = dob
        if (!gender?.trim().isNullOrEmpty()) user.gender = gender
        if (profile != null) {
            val savedImagePath = FileUtils.saveImage("User_$userId", profile)
            user.profile = savedImagePath
        }
        if (isBlocked != null) {
            if (isBlocked && reasonOfBlock == null) {
                return BaseResponse<User>().failed(
                    message = "Reason of block is required"
                )
            }
            user.isBlocked = isBlocked
        }
        if (!reasonOfBlock.isNullOrEmpty()) user.reasonOfBlock = reasonOfBlock

        if (fcm != null) {
            user.fcm = fcm
        }

        user.lastUpdate = TimeUtils.getCurrentDateTime()

        userService.save(user)
        return BaseResponse<User>().success(
            message = "Profile updated successfully",
            data = user
        )
    }

    @PostMapping(Endpoints.UPDATE_PROFILE_PICTURE)
    fun updateProfilePicture(
        @RequestParam("userId") userId: Long?,
        @RequestParam("profile") profile: MultipartFile?
    ): BaseResponse<User?> {
        if (profile == null || profile.isEmpty) return BaseResponse<User?>().failed(
            message = "Invalid file"
        )
        if (userId == null) return BaseResponse<User?>().failed(
            message = "User id required"
        )
        val user = userService.findById(userId)
            ?: return BaseResponse<User?>().failed(
                message = "User not found"
            )
        val file = FileUtils.saveImage("User_$userId", profile)
        if (file != null) {
            user.profile = file
            user.lastUpdate = TimeUtils.getCurrentDateTime()
            userService.save(user)
        }
        return if (file != null) BaseResponse<User?>().success(
            message = "Profile image updated successfully.",
            data = user
        ) else BaseResponse<User?>().failed(
            message = "Unable to update profile image."
        )
    }

    @PostMapping(Endpoints.GET_PROFILE)
    fun getProfile(
        @RequestParam("userId") userId: Long?
    ): BaseResponse<User?> {
        if (userId == null) return BaseResponse<User?>().failed(
            message = "User id required"
        )
        val user = userService.findById(userId)
            ?: return BaseResponse<User?>().failed(
                message = "User not found"
            )
        return BaseResponse<User?>().success(
            message = "Profile details fetched successfully.",
            data = user
        )
    }

    @PostMapping(Endpoints.DELETE_USER)
    fun deleteUser(
        @RequestParam("userId") userId: Long?
    ): BaseResponse<User?> {
        if (userId == null) return BaseResponse<User?>().failed(
            message = "User id required"
        )
        if (!userService.existsById(userId)) return BaseResponse<User?>().failed(
            message = "User not found"
        )

        userService.deleteUser(userId)
        return BaseResponse<User?>().success(
            message = "User deleted successfully.",
            data = null
        )
    }

    @PostMapping(Endpoints.GET_USER_LIST)
    fun getGetUsersList(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int,
        @RequestParam searchText: String?
    ): BaseResponse<List<User>?> {

        val pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "createdOn"))
        val users = userService.searchUser(searchText, pageable)
            ?: return BaseResponse<List<User>?>().failed(
                message = "No User found"
            )
        return BaseResponse<List<User>?>().success(
            message = "User list fetched successfully.",
            data = users.toList(),
            mTotalPage = users.totalPages,
            page = page
        )
    }

    @PostMapping(Endpoints.CHANGE_PASSWORD)
    fun changePassword(@RequestBody payload: ChangePasswordDto): BaseResponse<User> {
        if (payload.currentPassword == null) {
            return BaseResponse<User>().failed(
                message = "Current password is required"
            )
        }
        if (payload.password == null) {
            return BaseResponse<User>().failed(
                message = "Password is required"
            )
        }
        if (payload.password.length < 6) {
            return BaseResponse<User>().failed(
                message = "Password should contain at least 6 character"
            )
        }
        if (payload.userId == null) {
            return BaseResponse<User>().failed(
                message = "User id is required"
            )
        }
        if (!userService.existsById(payload.userId)) {
            return BaseResponse<User>().failed(
                message = "No user found with given user id"
            )
        }
        val user = userService.findById(payload.userId) ?: throw ApiException(404, "User not found")

        if (!hashService.checkBcrypt(payload.currentPassword, user.password)) {
            return BaseResponse<User>().failed(
                message = "Incorrect Password"
            )
        }

        user.password = hashService.hashBcrypt(payload.password)
        user.lastUpdate = TimeUtils.getCurrentDateTime()
        userService.save(user)
        return BaseResponse<User>().success(
            message = "Password reset successful",
            data = user
        )
    }

    @GetMapping(Endpoints.CHECK_PHONE_EXISTS)
    fun checkPhoneExists(@PathVariable("phone") phone: String): BaseResponse<Any?>? {

        return if (!userService.existsByPhone(phone)) BaseResponse<Any?>().failed(
            message = "Phone number not found in record"
        ) else
            BaseResponse<Any?>().success(
                message = "",
                data = userService.findByPhone(phone)
            )
    }

    @GetMapping(Endpoints.CHECK_USERNAME_EXISTS)
    fun checkUsernameExists(@PathVariable("username") username: String): BaseResponse<Any?>? {

        return if (!userService.existsByUsername(username)) BaseResponse<Any?>().failed(
            message = "Username not found in record"
        ) else
            BaseResponse<Any?>().success(
                message = "",
                data = null
            )
    }
}