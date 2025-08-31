package org.balanceeat.admin.api.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Balance-Eat Admin API")
                    .description("관리자용 API 문서")
                    .version("v1.0")
            )
            .servers(
                listOf(
                    Server().url("http://localhost:8080").description("로컬 개발 환경"),
                    Server().url("https://admin-api.balance-eat.com").description("운영 환경")
                )
            )
            .addSecurityItem(SecurityRequirement().addList("basicAuth"))
            .components(
                Components()
                    .addSecuritySchemes(
                        "basicAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("basic")
                            .description("관리자 기본 인증")
                    )
            )
    }
}