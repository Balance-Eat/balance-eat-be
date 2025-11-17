package org.balanceeat.api.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.util.ErrorHandler

@EnableScheduling
@Configuration
class ScheduledConfig : SchedulingConfigurer {

    @Bean
    fun taskScheduler(): TaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()

        // CPU 코어 수 기반으로 풀 크기 계산
        val availableProcessors = Runtime.getRuntime().availableProcessors()
        val poolSize = availableProcessors.coerceAtLeast(2)

        scheduler.poolSize = poolSize
        scheduler.setThreadNamePrefix("Scheduled-Task-")
        scheduler.setErrorHandler(SchedulerExceptionHandler())
        scheduler.initialize()

        return scheduler
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler())
    }

    class SchedulerExceptionHandler : ErrorHandler {
        private val log = LoggerFactory.getLogger(SchedulerExceptionHandler::class.java)

        override fun handleError(ex: Throwable) {
            log.error(
                "Scheduled task execution error: {}",
                ex.message,
                ex
            )
        }
    }
}