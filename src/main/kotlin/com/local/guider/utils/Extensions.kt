package com.local.guider.utils

fun String?.mkFirstUppercase(): String? {
    return this?.let { if (it.isNotEmpty()) it.substring(0, 1).uppercase() + it.substring(1) else it }
}