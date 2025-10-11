package com.local.guider.network_utils

object Endpoints {

    const val START_NODE = "/api"

    // Authentication APIs
    const val REGISTER = "/user/register"
    const val LOGIN = "/user/login"
    const val FORGET_PASSWORD =  "/user/forget_password"
    const val UPDATE_PROFILE =  "/user/update_profile"
    const val GET_PROFILE =  "/user/get_profile"
    const val DELETE_USER =  "/user/delete"
    const val GET_USER_LIST =  "/user/get_user_list"
    const val UPDATE_PROFILE_PICTURE =  "/user/update_profile_picture"
    const val CHANGE_PASSWORD = "/user/change_password"
    const val CHECK_PHONE_EXISTS = "/user/check_phone_exist/phone={phone}"
    const val CHECK_USERNAME_EXISTS = "/user/check_username_exist/username={username}"
    const val ADD_BALANCE = "/user/add_balance"

    // Photographer APIs
    const val REQUEST_FOR_PHOTOGRAPHER = "/photographers/request"
    const val REQUEST_TEST = "/photographers/test"
    const val CHANGE_PHOTOGRAPHER_ACTIVE_STATUS = "/photographers/change_active_status"
    const val RESPOND_PHOTOGRAPHER_REQUEST = "/photographers/respond_on_request"
    const val UPDATE_PHOTOGRAPHER = "/photographers/update"
    const val DELETE_PHOTOGRAPHER = "/photographers/delete"
    const val GET_PHOTOGRAPHERS_BY_PLACE_ID = "/photographers/get_by_place"
    const val GET_PHOTOGRAPHERS_ALL = "/photographers/get_all"
    const val GET_PHOTOGRAPHERS_DETAILS = "/photographers/details"
    const val GET_PHOTOGRAPHERS_REQUESTS = "/photographers/all_requests"

    // Guider APIs
    const val REQUEST_FOR_GUIDER = "/guider/request"
    const val RESPOND_GUIDER_REQUEST = "/guider/respond_on_request"
    const val CHANGE_GUIDER_ACTIVE_STATUS = "/guider/change_active_status"
    const val UPDATE_GUIDER = "/guider/update"
    const val DELETE_GUIDER = "/guider/delete"
    const val GET_GUIDERS_BY_PLACE_ID = "/guider/get_by_place"
    const val GET_GUIDERS_ALL = "/guider/get_all"
    const val GET_GUIDERS_DETAILS = "/guider/details"

    // Images APIs
    const val ADD_IMAGE = "/images/add"
    const val ALL_IMAGES = "/images/get_all"
    const val ALL_IMAGES_BY_ID = "/images/get_by_id"
    const val DELETE_IMAGE = "/images/delete"
    const val READ_IMAGES = "/Uploads/{imagePath:.+}"
    const val DOWNLOAD_IMAGE = "/image/download/{path}"

    // Places APIs
    const val ADD_PLACE = "/places/add"
    const val EDIT_PLACE = "/places/edit"
    const val GET_PLACES = "/places/get"
    const val GET_PLACES_BY_IDS = "/places/get_by_ids"
    const val DELETE_PLACE = "/places/delete"
    const val ADD_VIEW = "/places/add_view"

    // Time Slots APIs
    const val ADD_TIMESLOT = "/timeslots/add"
    const val DELETE_TIMESLOT = "/timeslots/delete"
    const val GET_ALL_TIMESLOTS = "/timeslots/get_all"

    // Review APIs
    const val ADD_REVIEW = "/review/add"
    const val DELETE_REVIEW = "/review/delete"
    const val GET_ALL_REVIEW = "/review/get_all"

    // Appointments APIs
    const val CREATE_APPOINTMENT = "/appointment/create"
    const val EDIT_APPOINTMENT = "/appointment/edit"
    const val DELETE_APPOINTMENT = "/appointment/delete"
    const val RESPOND_APPOINTMENT = "/appointment/respond"
    const val GET_APPOINTMENT_BY_TRANSACTION_ID = "/appointment/get_by_transaction_id"
    const val USER_CANCEL_APPOINTMENT = "/appointment/cancel_by_user"
    const val GET_APPOINTMENTS = "/appointment/get_all"

    // Home APIs
    const val GET_HOME = "/main/home_details"
    const val ADMIN_DASHBOARD = "/main/admin_dashboard"


    // Services APIs
    const val CREATE_SERVICE = "/service/create"
    const val UPDATE_SERVICE = "/service/update"
    const val GET_SERVICES = "/service/get"
    const val DELETE_SERVICE = "/service/delete"

    // Transactions
    const val CREATE_TRANSACTION = "/transaction/create"
    const val UPDATE_TRANSACTION = "/transaction/update"
    const val GET_TRANSACTION = "/transaction/get"
    const val DELETE_TRANSACTION = "/transaction/delete"

    // Transactions
    const val CREATE_WITHDRAWAL = "/withdrawal/create"
    const val RESPOND_WITHDRAWAL = "/withdrawal/respond"
    const val GET_WITHDRAWAL = "/withdrawal/get"


    // Settings
    const val UPDATE_SETTINGS = "/settings/update"
    const val GET_SETTINGS = "/settings/get"

    // For Play Console
    const val PRIVACY_POLICY = "/privacy_policy"

    // Maps API
    const val MAP_GET_PLACES = "/map/get_places"
    const val MAP_GET_LAT_LNG = "/map/get_lat_lng"

    // File Downloads
    const val DOWNLOAD_USERS = "download/users"
    const val DOWNLOAD_PHOTOGRAPHERS = "download/photographers"
    const val DOWNLOAD_GUIDERS = "download/guiders"
    const val DOWNLOAD_PLACES = "download/places"
    const val DOWNLOAD_TRANSACTIONS = "download/transactions"
    const val DOWNLOAD_WITHDRAWALS = "download/withdrawals"

    // Notification
    const val GET_NOTIFICATIONS = "notification/get_by_id"
    const val CREATE_NOTIFICATION = "notification/create"
    const val MARK_AS_READ_NOTIFICATION = "notification/mark_as_read"
    const val DELETE_NOTIFICATION = "notification/delete"


}