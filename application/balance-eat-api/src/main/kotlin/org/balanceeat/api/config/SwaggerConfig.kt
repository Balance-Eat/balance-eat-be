package org.balanceeat.api.config

import org.springdoc.core.configuration.SpringDocConfiguration
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springdoc.core.providers.ObjectMapperProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class SwaggerConfig {
    @Primary
    @Bean
    fun springDocConfiguration(): SpringDocConfiguration {
        return SpringDocConfiguration()
    }

    @Bean
    fun springDocConfigProperties(): SpringDocConfigProperties {
        return SpringDocConfigProperties()
    }

    @Bean
    fun objectMapperProvider(springDocConfigProperties: SpringDocConfigProperties): ObjectMapperProvider {
        return ObjectMapperProvider(springDocConfigProperties)
    }
}
