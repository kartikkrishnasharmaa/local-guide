package com.local.guider.controller

import com.google.gson.Gson
import com.local.guider.entities.Review
import com.local.guider.models.BaseResponse
import com.local.guider.models.response.ReviewResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.services.*
import com.local.guider.utils.TimeUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class ReviewController(
    private val userService: UserService,
    private val placeService: PlaceService,
    private val photographerService: PhotographerService,
    private val guiderService: GuiderService,
    private val reviewService: ReviewService,
    private val appointmentService: AppointmentService
) {

    @PostMapping(Endpoints.ADD_REVIEW)
    fun addReview(
        @RequestParam("rating") rating: Double?,
        @RequestParam("message") message: String?,
        @RequestParam("userId") userId: Long?,
        @RequestParam("photographerId") photographerId: Long? = null,
        @RequestParam("guiderId") guiderId: Long? = null,
        @RequestParam("placeId") placeId: Long? = null
    ): BaseResponse<Review?> {
        if (rating == null) {
            return BaseResponse<Review?>().failed(message = "Rating is required.")
        }

        val user = userService.findById(userId ?: -1) ?: return BaseResponse<Review?>().failed(message = "User not found")

        val review = Review()
        review.rating = rating
        review.message = message
        review.message = message
        review.userId = user.id
        review.createdOn = TimeUtils.getCurrentDateTime()
        review.lastUpdate = TimeUtils.getCurrentDateTime()

        var newRating = 0.0

        if (photographerId != null) {
            if (!photographerService.existsById(photographerId)) return BaseResponse<Review?>().failed(
                message = "Invalid photographer id!"
            )
            val photographer = photographerService.findById(photographerId)
            photographer?.rating = ((photographer?.rating ?: 0.0) + rating) / if(photographer?.rating != null && photographer.rating != 0.0) 2 else 1
            photographerService.save(photographer!!)
            review.photographerId = photographerId
            newRating = photographer.rating ?: 0.0
        } else if (guiderId != null) {
            if (!guiderService.existsById(guiderId)) return BaseResponse<Review?>().failed(
                message = "Invalid guider id!"
            )
            val guider = guiderService.findById(guiderId)
            guider?.rating = ((guider?.rating ?: 0.0) + rating) / if(guider?.rating != null && guider.rating != 0.0) 2 else 1
            guiderService.save(guider!!)
            review.guiderId = guiderId
            newRating = guider.rating ?: 0.0
        } else if (placeId != null) {
            if (!placeService.existById(placeId)) return BaseResponse<Review?>().failed(
                message = "Invalid place id!"
            )
            val place = placeService.findById(placeId)
            place?.rating = ((place?.rating ?: 0.0) + rating) / if(place?.rating != null && place.rating != 0.0) 2 else 1
            placeService.save(place!!)
            review.placeId = placeId
            newRating = place.rating ?: 0.0
        }

        review.newRating = newRating
        reviewService.save(review)

        return BaseResponse<Review?>().success(
            message = "Review added successfully.",
            data = review
        )
    }

    @PostMapping(Endpoints.GET_ALL_REVIEW)
    fun getReviewsById(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int,
        @RequestParam("userId") userId: Long? = null,
        @RequestParam("photographerId") photographerId: Long? = null,
        @RequestParam("guiderId") guiderId: Long? = null,
        @RequestParam("placeId") placeId: Long? = null
    ): BaseResponse<List<ReviewResponse>?> {
        if (photographerId == null && guiderId == null && placeId == null) return BaseResponse<List<ReviewResponse>?>().failed(
            message = "No Id specified"
        )
        val pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "rating"))
        var reviews: Page<Review>? = null
        val userReviews: List<Review>?

        var totalAppointments = 0
        var canReview = false

        if (photographerId != null) {
            if (!photographerService.existsById(photographerId)) return BaseResponse<List<ReviewResponse>?>().failed(
                message = "Invalid photographer id!"
            )
            reviews = reviewService.findByPhotographerId(photographerId, pageable)
                ?: return BaseResponse<List<ReviewResponse>?>().failed(
                    message = "No review found"
                )
            if (userId != null) {
                userReviews = reviewService.findByPhotographerId(photographerId, userId)
                totalAppointments = appointmentService.findPhotographerIdAndUserId(photographerId, userId)?.size ?: 0
                println(">>>>>>>><><><><><><><><  $totalAppointments  ${userReviews?.size}")
                canReview = (userReviews?.size ?: 0) < totalAppointments
            }
        } else if (guiderId != null) {
            if (!guiderService.existsById(guiderId)) return BaseResponse<List<ReviewResponse>?>().failed(
                message = "Invalid guider id!"
            )
            reviews = reviewService.findByGuiderId(guiderId, pageable)
                ?: return BaseResponse<List<ReviewResponse>?>().failed(
                    message = "No review found"
                )
            if (userId != null) {
                userReviews = reviewService.findByGuiderId(guiderId, userId)
                totalAppointments = appointmentService.findGuiderIdAndUserId(guiderId, userId)?.size ?: 0
                canReview = (userReviews?.size ?: 0) < totalAppointments
            }
        } else if (placeId != null) {
            if (!placeService.existById(placeId)) return BaseResponse<List<ReviewResponse>?>().failed(
                message = "Invalid place id!"
            )
            reviews = reviewService.findByPlaceId(placeId, pageable)
                ?: return BaseResponse<List<ReviewResponse>?>().failed(
                    message = "No review found"
                )
            if (userId != null) {
                userReviews = reviewService.findByPlaceId(placeId, userId)
                canReview = userReviews.isNullOrEmpty()
            }
        }

        val list = ArrayList<ReviewResponse>()
        reviews?.forEach {
            val user = if (it.userId != null) userService.findById(it.userId!!) else null
            val fullName = user?.name ?: "Unknown"
            val profileImage = user?.profile
            val response = Gson().fromJson(Gson().toJson(it), ReviewResponse::class.java)
            response.fullName = fullName
            response.profileImage = profileImage
            list.add(response)
        }
        return BaseResponse<List<ReviewResponse>?>().success(
            message = "Review fetched successfully",
            canGiveReview = canReview,
            data = list
        )
    }

    @DeleteMapping(Endpoints.DELETE_REVIEW)
    fun deleteSlot(
        @RequestParam("id") id: Long?
    ): BaseResponse<Any?> {
        if (id == null) {
            return BaseResponse<Any?>().failed(
                message = "Slot id is required."
            )
        }
        reviewService.deleteById(id)
        return BaseResponse<Any?>().success(
            message = "Review deleted successfully.",
            data = null
        )
    }

}