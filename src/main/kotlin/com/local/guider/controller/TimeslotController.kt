package com.local.guider.controller

import com.local.guider.entities.Timeslot
import com.local.guider.models.BaseResponse
import com.local.guider.network_utils.Endpoints
import com.local.guider.services.TimeslotService
import com.local.guider.utils.TimeUtils
import org.springframework.web.bind.annotation.*

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class TimeslotController(
    private val timeslotService: TimeslotService
) {

    @PostMapping(Endpoints.ADD_TIMESLOT)
    fun addSlot(
        @RequestParam("startTime") startTime: String?,
        @RequestParam("endTime") endTime: String?
    ): BaseResponse<Timeslot?> {
        if (startTime == null || endTime == null) {
            return BaseResponse<Timeslot?>().failed(message = "Start time and End time is required.")
        }
        val dtStartTime = TimeUtils.stringToDate(startTime)
        val dtEndTime = TimeUtils.stringToDate(endTime)
        val title = TimeUtils.formatDate(dtStartTime, TimeUtils.FORMAT_hh_mm_a) + " - " + TimeUtils.formatDate(
            dtEndTime,
            TimeUtils.FORMAT_hh_mm_a
        )
        val newTimeslot = Timeslot()
        newTimeslot.startTime = dtStartTime
        newTimeslot.endTime = dtEndTime
        newTimeslot.title = title
        newTimeslot.createdOn = TimeUtils.getCurrentDateTime()
        newTimeslot.lastUpdate = TimeUtils.getCurrentDateTime()
        timeslotService.save(newTimeslot)

        return BaseResponse<Timeslot?>().success(
            message = "Timeslot created successfully.",
            data = newTimeslot
        )
    }

    @GetMapping(Endpoints.GET_ALL_TIMESLOTS)
    fun getSlots(): BaseResponse<List<Timeslot>> {
        return BaseResponse<List<Timeslot>>().success(
            message = "Timeslots fetched successfully.",
            data = timeslotService.findAll() ?: emptyList()
        )
    }

    @DeleteMapping(Endpoints.DELETE_TIMESLOT)
    fun deleteSlot(
        @RequestParam("slotId") slotId: Long?
    ): BaseResponse<Any?> {
        if (slotId == null) {
            return BaseResponse<Any?>().failed(
                message = "Slot id is required."
            )
        }
        timeslotService.deleteById(slotId)
        return BaseResponse<Any?>().success(
            message = "Timeslot deleted successfully.",
            data = null
        )
    }

}