package com.local.guider.utils

import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object TimeUtils {

    const val FORMAT_yyyy_MM_dd_hh_mm_a = "yyyy-MM-dd hh:mm a"
    const val FORMAT_hh_mm_a = "hh:mm a"

    fun getCurrentDateTime(): Date {
        return Date()
    }

    fun stringToDate(input: String): Date {
        val zonedDateTime = ZonedDateTime.parse(input, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val istDateTime = zonedDateTime.withZoneSameInstant(TimeZone.getTimeZone("Asia/Kolkata").toZoneId())
        return Date.from(istDateTime.toInstant())
    }

    fun stringToDate(input: String, format: String): Date {
         return SimpleDateFormat(format, Locale.ENGLISH).parse(input)
    }

    fun formatDate(date: Date, format: String): String {
        return SimpleDateFormat(format, Locale.ENGLISH).format(date)
    }
}