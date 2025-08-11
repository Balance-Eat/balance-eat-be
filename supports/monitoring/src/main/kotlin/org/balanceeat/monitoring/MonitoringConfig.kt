package org.balanceeat.monitoring

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.config.MeterFilter
import mu.KotlinLogging
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

private val logger = KotlinLogging.logger {}

@Configuration
class MonitoringConfig {
    @Bean
    fun timedAspect(registry: MeterRegistry): TimedAspect {
        return TimedAspect(registry)
    }

    @Bean
    fun metricsCommonTags(environment: Environment): MeterRegistryCustomizer<MeterRegistry> {
        val applicationName = environment.getProperty("spring.application.name", "balance-eat")
        val profiles = environment.activeProfiles.joinToString(",")

        return MeterRegistryCustomizer { registry ->
            registry.config()
                .commonTags(
                    "application",
                    applicationName,
                    "profiles",
                    profiles,
                )
                .meterFilter(
                    MeterFilter.deny { id ->
                        // 불필요한 메트릭 필터링
                        val name = id.name
                        name.startsWith("jvm.gc.pause") ||
                            name.startsWith("jvm.buffer") ||
                            name.startsWith("process.uptime")
                    },
                )
        }
    }

    @Bean("customHealthIndicator")
    fun customHealthIndicator(): HealthIndicator {
        return HealthIndicator {
            try {
                // 실제 헬스체크 로직을 여기에 구현
                val freeMemory = Runtime.getRuntime().freeMemory()
                val totalMemory = Runtime.getRuntime().totalMemory()
                val usedMemory = totalMemory - freeMemory
                val memoryUsage = (usedMemory.toDouble() / totalMemory.toDouble()) * 100

                if (memoryUsage > 90) {
                    Health.down()
                        .withDetail("memory_usage", "${memoryUsage.toInt()}%")
                        .withDetail("reason", "High memory usage")
                        .build()
                } else {
                    Health.up()
                        .withDetail("memory_usage", "${memoryUsage.toInt()}%")
                        .withDetail("status", "healthy")
                        .build()
                }
            } catch (e: Exception) {
                logger.error(e) { "Health check failed" }
                Health.down()
                    .withDetail("error", e.message)
                    .build()
            }
        }
    }
}
