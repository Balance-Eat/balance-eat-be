package org.balanceeat.apibase.config

import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}

@Configuration
class BaseWebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)

        logger.info { "CORS configuration applied" }
    }

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(StringToLocalDateConverter())
        registry.addConverter(StringToLocalDateTimeConverter())
        logger.info { "Custom formatters registered" }
    }

    private class StringToLocalDateConverter : Converter<String, LocalDate> {
        override fun convert(source: String): LocalDate {
            return try {
                LocalDate.parse(source, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: Exception) {
                logger.warn { "Failed to parse date: $source, using ISO_DATE format" }
                LocalDate.parse(source, DateTimeFormatter.ISO_DATE)
            }
        }
    }

    private class StringToLocalDateTimeConverter : Converter<String, LocalDateTime> {
        override fun convert(source: String): LocalDateTime {
            return try {
                LocalDateTime.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            } catch (e: Exception) {
                logger.warn { "Failed to parse datetime: $source, using ISO_DATE_TIME format" }
                LocalDateTime.parse(source, DateTimeFormatter.ISO_DATE_TIME)
            }
        }
    }
}