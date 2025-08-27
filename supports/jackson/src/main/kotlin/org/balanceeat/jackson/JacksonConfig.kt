package org.balanceeat.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class JacksonConfig {
    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return ObjectMapperFactory.getGlobalObjectMapper()
    }
}
