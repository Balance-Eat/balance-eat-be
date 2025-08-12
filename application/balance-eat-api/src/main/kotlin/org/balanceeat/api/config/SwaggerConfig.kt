package org.balanceeat.api.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
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
                    .title("Balance Eat API")
                    .version("1.0.0")
            )
            .servers(
                listOf(
                    Server()
                        .url("http://localhost:8080")
                        .description("Local Development Server"),
                    Server()
                        .url("https://api.balanceeat.com")
                        .description("Production Server")
                )
            )
            .addSecurityItem(
                SecurityRequirement().addList("bearerAuth")
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearerAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("JWT 토큰을 입력하세요")
                    )
                    .addRequestBodies(
                        "defaultJsonRequestBody",
                        RequestBody()
                            .content(
                                Content()
                                    .addMediaType(
                                        "application/json",
                                        MediaType()
                                            .schema(Schema<Any>().type("object"))
                                    )
                            )
                            .required(true)
                    )
                    .addResponses(
                        "defaultJsonResponse",
                        ApiResponse()
                            .description("Default JSON Response")
                            .content(
                                Content()
                                    .addMediaType(
                                        "application/json",
                                        MediaType()
                                            .schema(Schema<Any>().type("object"))
                                    )
                            )
                    )
            )
    }
}
