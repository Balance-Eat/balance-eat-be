package org.balanceeat.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class ApplicationBeanConfig {
    @Bean
    open fun restClient(): RestClient {
        return RestClient.builder().build()
    }
}