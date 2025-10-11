package com.local.guider

import com.local.guider.network_utils.Endpoints
import com.local.guider.services.TokenService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val tokenService: TokenService,
) {

    @Bean
    fun customAuthenticationEntryPoint() : CustomAuthenticationEntryPoint {
        return CustomAuthenticationEntryPoint()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        // Define public and private routes
        http.authorizeHttpRequests {
            it.requestMatchers(HttpMethod.POST, Endpoints.START_NODE + Endpoints.LOGIN).permitAll()
                .requestMatchers(HttpMethod.POST, Endpoints.START_NODE + Endpoints.REGISTER).permitAll()
                .requestMatchers(HttpMethod.POST, Endpoints.START_NODE + Endpoints.FORGET_PASSWORD).permitAll()
                .requestMatchers(HttpMethod.GET, Endpoints.START_NODE + Endpoints.CHECK_PHONE_EXISTS).permitAll()
                .requestMatchers(HttpMethod.GET, Endpoints.START_NODE + Endpoints.CHECK_USERNAME_EXISTS).permitAll()
                .requestMatchers(HttpMethod.GET, Endpoints.START_NODE + Endpoints.READ_IMAGES).permitAll()
                .requestMatchers(HttpMethod.POST, Endpoints.START_NODE + Endpoints.MAP_GET_PLACES).permitAll()
                .requestMatchers(HttpMethod.POST, Endpoints.START_NODE + Endpoints.GET_PLACES).permitAll()
                .requestMatchers(HttpMethod.POST, Endpoints.START_NODE + Endpoints.GET_SETTINGS).permitAll()
                .requestMatchers(HttpMethod.GET, Endpoints.START_NODE + "/image/download/{path}").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest()
                .permitAll()
        }

        // Configure JWT
        http.oauth2ResourceServer {
            it.jwt {

            }
        }

        http.authenticationManager { auth ->
            val jwt = auth as BearerTokenAuthenticationToken
            val user = tokenService.parseToken(jwt.token) ?: throw InvalidBearerTokenException("Invalid token")
            UsernamePasswordAuthenticationToken(user, "", listOf(SimpleGrantedAuthority("USER")))
        }

        // Other configuration
        http.cors { }
        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        http.csrf {
            it.disable()
        }
        http.headers {
            it.frameOptions {
                it.disable()
            }
            it.xssProtection {
                it.disable()
            }
        }
//        http.exceptionHandling {
//            it.authenticationEntryPoint(customAuthenticationEntryPoint())
//        }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        // allow localhost for dev purposes
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        configuration.allowedHeaders = listOf("authorization", "content-type")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}