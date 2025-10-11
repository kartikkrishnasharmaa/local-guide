package com.local.guider

import com.local.guider.network_utils.Endpoints
import com.local.guider.services.SettingsService
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@SpringBootApplication
@RestController
class LocalGuiderApplication(
	private val settingsService: SettingsService
) {

	@GetMapping("/")
	fun helloWorld(): String {
		return "Hello! Local Guider ${Date()}"
	}

	@GetMapping("/error")
	fun onError(): String {
		return "Something went wrong"
	}

	@GetMapping(Endpoints.PRIVACY_POLICY)
	fun getPrivacyPolicy(): String {
		return "<html>\n" + "<header><title>Welcome</title></header>\n" +
				"<body>\n" + "${settingsService.getFirst().privacyPolicy}\n" + "</body>\n" + "</html>"
	}

	@PostConstruct
	fun init() {
		TimeZone.setDefault(TimeZone.getTimeZone("IST"))
	}

}

fun main(args: Array<String>) {
	runApplication<LocalGuiderApplication>(*args)
}