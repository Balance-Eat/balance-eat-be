package org.balanceeat.jackson

import com.fasterxml.jackson.core.type.TypeReference

object JsonUtils {
    
    private val objectMapper = ObjectMapperFactory.getGlobalObjectMapper()
    
    fun stringify(value: Any): String {
        return objectMapper.writeValueAsString(value)
    }
    
    inline fun <reified T> parse(json: String): T {
        return ObjectMapperFactory.getGlobalObjectMapper().readValue(json, T::class.java)
    }

    fun <T> parse(json: String, clazz: Class<T>): T {
        return objectMapper.readValue(json, clazz)
    }
    
    fun <T> parse(json: String, typeReference: TypeReference<T>): T {
        return objectMapper.readValue(json, typeReference)
    }
    
    fun parseToMap(json: String): Map<String, Any> {
        return objectMapper.readValue(json, object : TypeReference<Map<String, Any>>() {})
    }

    inline fun <reified T> parseToList(json: String): List<T> {
        return ObjectMapperFactory.getGlobalObjectMapper().readValue(json, object : TypeReference<List<T>>() {})
    }
}