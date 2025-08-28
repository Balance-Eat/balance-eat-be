package org.balanceeat.api.config

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.lang.reflect.Method
import java.util.concurrent.Executor

@EnableAsync
@Configuration
class AsyncConfiguration : AsyncConfigurer {

    @Bean
    fun asyncExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()

        // CPU 코어 수 기반으로 풀 크기 계산
        val availableProcessors = Runtime.getRuntime().availableProcessors()
        val corePoolSize = availableProcessors * 2
        val maxPoolSize = availableProcessors * 4

        executor.corePoolSize = corePoolSize
        executor.maxPoolSize = maxPoolSize
        executor.setQueueCapacity(queueCapacity())
        executor.setThreadNamePrefix("Async-Executor-")
        executor.initialize()
        return executor
    }

    override fun getAsyncExecutor(): Executor = asyncExecutor()

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler = AsyncExceptionHandler()

    private fun queueCapacity(): Int {
        // JVM이 사용할 수 있는 최대 메모리 크기(바이트 단위)
        val maxMemory = Runtime.getRuntime().maxMemory()

        // 메모리 크기를 MB 단위로 변환
        val maxMemoryInMB = (maxMemory / (1024 * 1024)).toInt()

        // 메모리 크기에 따라 작업 큐 크기 계산
        return when {
            maxMemoryInMB <= 1024 -> 200      // 1GB 이하 메모리: 작은 큐 크기
            maxMemoryInMB <= 4096 -> 500      // 1GB ~ 4GB 메모리: 중간 큐 크기
            else -> 1000                      // 4GB 이상 메모리: 큰 큐 크기
        }
    }

    class AsyncExceptionHandler : AsyncUncaughtExceptionHandler {
        private val log = LoggerFactory.getLogger(AsyncExceptionHandler::class.java)
        override fun handleUncaughtException(ex: Throwable, method: Method, vararg params: Any) {
            log.error("[Async] {}", ex.message, ex)
        }
    }
}