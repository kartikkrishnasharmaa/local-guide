package com.local.guider.dto

import org.springframework.web.multipart.MultipartFile

class ServiceDto {
    var title: String? = null
    var description: String? = null
    var image: MultipartFile? = null
    var servicePrice: Double? = null
}