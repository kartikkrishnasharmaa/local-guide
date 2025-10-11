package com.local.guider.models.response

import com.local.guider.entities.Guider
import com.local.guider.entities.Photographer
import com.local.guider.entities.Place

class HomeResponse {
    var places: List<Place>? = null
    var guiders: List<Guider>? = null
    var photographers: List<Photographer>? = null
    var privacyPolicy: String? = null
    var termsAndConditions: String? = null
    var contactUs: String? = null
    var aboutUs: String? = null
    var appLink: String? = null
}