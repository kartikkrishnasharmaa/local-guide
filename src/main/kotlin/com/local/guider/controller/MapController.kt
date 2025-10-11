package com.local.guider.controller

import com.local.guider.network_utils.Endpoints
import com.local.guider.services.MapService
import org.springframework.web.bind.annotation.*

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(Endpoints.START_NODE)
class MapController(
    private val mapApiService: MapService
) {

    @RequestMapping(Endpoints.MAP_GET_PLACES, method = [RequestMethod.POST])
    fun getMapData(@RequestParam searchText: String, @RequestParam cities: Boolean = false): Any {
        val places = mapApiService.getMapData(searchText, cities)
        places.predictions?.forEach {
            if (it.place_id != null) {
                val latLng = mapApiService.getLatLngByPlaceId(it.place_id!!)
                it.latitude = latLng.result?.geometry?.location?.lat
                it.longitude = latLng.result?.geometry?.location?.lng
                it.mapUrl = latLng.result?.url
            }
        }
        return places
    }

}