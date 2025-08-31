package org.balanceeat.jackson

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.time.ZoneId
import java.util.TimeZone

object ObjectMapperFactory {
    
    private val globalObjectMapper: ObjectMapper = createObjectMapper()
    
    fun getGlobalObjectMapper(): ObjectMapper = globalObjectMapper
    
    private fun createObjectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(JavaTimeModule())
            .registerKotlinModule()
            .setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Seoul")))
            .apply {
                // 직렬화 설정
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
                configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true)
                configure(SerializationFeature.INDENT_OUTPUT, false)

                // 역직렬화 설정
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)

                // 프로퍼티 네이밍 전략
                propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE

                // null 값 제외
                setSerializationInclusion(JsonInclude.Include.ALWAYS)

                // 필드 접근 설정
                setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)
            }
    }
}