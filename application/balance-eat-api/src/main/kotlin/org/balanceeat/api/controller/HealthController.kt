package org.balanceeat.api.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.balanceeat.api.config.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "Health Check API")
@RestController
@RequestMapping("/health")
class HealthController {
    @GetMapping
    fun health(): ApiResponse<Map<String, Any>> {
        val healthData =
            mapOf(
                "status" to "UP",
                "timestamp" to LocalDateTime.now(),
                "service" to "balance-eat-api",
                "version" to "1.0.0",
            )

        return ApiResponse.success(healthData, "서비스가 정상적으로 작동중입니다")
    }
}
