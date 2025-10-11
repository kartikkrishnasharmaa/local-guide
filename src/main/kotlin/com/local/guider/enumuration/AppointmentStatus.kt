package com.local.guider.enumuration;

enum class AppointmentStatus(val key: String, val notificationMsg: String) {
 REQUESTED("requested", "Appointed request has been sent successfully 🤩."),
 CANCELED("canceled", "We are sorry 😔. Your appointment was canceled ❌. Please check the reason in the appointment section.👍🏻"),
 ACCEPTED("accepted", "Your appointment was accepted ✅"),
 ONGOING("on_going", "Enjoy Local Guider Services 🥰"),
 COMPLETED("completed", "Your appointment was completed 🎉. Thanks for using Local Guider😍."),
}
