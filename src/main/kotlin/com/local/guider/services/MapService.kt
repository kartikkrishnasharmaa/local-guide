package com.local.guider.services

import com.local.guider.models.response.LatLngResponse
import com.local.guider.models.response.PredicationResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class MapService(
    private val restTemplate: RestTemplate,
    @Value("\${map.api.url}") private val mapApiUrl: String,
    @Value("\${map.api.key}") private val apiKey: String
) {

    fun getMapData(input: String, isCitiesOnly: Boolean = false): PredicationResponse {
        val url = mapApiUrl +  "place/autocomplete/json?input=$input${if(isCitiesOnly) "&types=(cities)&components=country:IN" else ""}&key=$apiKey"
        return restTemplate.getForObject(url, PredicationResponse::class.java) ?: throw RuntimeException("Failed to fetch map data")
    }

    fun getLatLngByPlaceId(placeId: String): LatLngResponse {
        val url = mapApiUrl + "place/details/json?placeid=$placeId&key=$apiKey"
        return restTemplate.getForObject(url, LatLngResponse::class.java) ?: throw RuntimeException("Failed to fetch map data")
    }

}