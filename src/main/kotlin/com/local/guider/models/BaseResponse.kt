package com.local.guider.models

class BaseResponse<T> {

    var status: Boolean = true
    var message: String = ""
    var canGiveReview = true
    var page: Int = 0
    var totalPages: Int = 0
    var data: T? = null

    fun success(message: String? = null, page: Int = 0, mTotalPage: Int = 0, data: T, canGiveReview: Boolean = true): BaseResponse<T> {
        this.status = true
        this.message = message ?: "Successful"
        this.data = data
        this.page = page
        this.canGiveReview = canGiveReview
        this.totalPages = mTotalPage
        return this
    }

     fun failed(message: String? = null, data: T? = null): BaseResponse<T> {
        this.status = false
        this.message = message ?: "Failed"
        this.data = data
        return this
    }

    fun unauthorized(): BaseResponse<T> {
        this.status = false
        this.message = "Unauthorized"
        this.data = null
        return this
    }



}