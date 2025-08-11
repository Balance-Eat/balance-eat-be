package org.balanceeat.api.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

/**
 * 데이터베이스 설정 정보를 모니터링하는 설정 클래스
 * 실제 DataSource는 Spring Boot가 application.yml 설정을 기반으로 자동 구성
 */
@Configuration
class DatabaseConfig(
    private val environment: Environment,
    @Value("\${spring.datasource.url:not-configured}") private val dbUrl: String,
    @Value("\${spring.datasource.username:not-configured}") private val dbUsername: String,
    @Value("\${spring.datasource.driver-class-name:not-configured}") private val dbDriver: String,
) {
    @PostConstruct
    fun printDatabaseInfo() {
        val profile = environment.activeProfiles.firstOrNull() ?: "default"

        println("🔗 Database Configuration (IntelliJ Style):")
        println("   - Profile: $profile")
        println("   - URL: ${maskSensitiveInfo(dbUrl)}")
        println("   - Username: ${maskSensitiveInfo(dbUsername)}")
        println("   - Driver: $dbDriver")
        println("   💡 Environment variables loaded from: $profile.env")
    }

    private fun maskSensitiveInfo(value: String): String {
        return when {
            value.contains("://") -> value.replace(Regex("//[^/]+"), "//***")
            value.length > 8 -> "${value.take(4)}***${value.takeLast(2)}"
            else -> "***"
        }
    }
}
