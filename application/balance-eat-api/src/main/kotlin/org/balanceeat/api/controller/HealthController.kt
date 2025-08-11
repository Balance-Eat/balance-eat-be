package org.balanceeat.api.controller

import org.balanceeat.api.config.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

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

    @GetMapping("/ready")
    fun ready(): ApiResponse<Map<String, String>> {
        val readyData = mapOf("status" to "READY")
        return ApiResponse.success(readyData, "서비스가 요청을 받을 준비가 되었습니다")
    }
}
