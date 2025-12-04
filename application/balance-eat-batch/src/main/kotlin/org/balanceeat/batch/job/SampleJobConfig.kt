package org.balanceeat.batch.job

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDateTime

@Configuration
class SampleJobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager
) {
    private val log = LoggerFactory.getLogger(SampleJobConfig::class.java)

    @Bean
    fun sampleJob(): Job {
        return JobBuilder("sampleJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(taskletStep(null))
            .next(loggingStep())
            .build()
    }

    @Bean
    @JobScope
    fun taskletStep(
        @Value("#{jobParameters['message']}") message: String?
    ): Step {
        return StepBuilder("taskletStep", jobRepository)
            .tasklet({ contribution, chunkContext ->
                val executionMessage = message ?: "기본 메시지"
                val currentTime = LocalDateTime.now()

                log.info("=== Tasklet Step 실행 ===")
                log.info("실행 시간: $currentTime")
                log.info("전달받은 메시지: $executionMessage")
                log.info("Job 이름: ${chunkContext.stepContext.jobName}")
                log.info("Step 이름: ${chunkContext.stepContext.stepName}")

                contribution.incrementReadCount()
                contribution.incrementWriteCount(1)

                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean
    fun loggingStep(): Step {
        return StepBuilder("loggingStep", jobRepository)
            .tasklet({ contribution, chunkContext ->
                log.info("=== Logging Step 실행 ===")
                log.info("이전 Step 실행 결과: SUCCESS")
                log.info("배치 작업이 정상적으로 완료되었습니다.")

                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
}