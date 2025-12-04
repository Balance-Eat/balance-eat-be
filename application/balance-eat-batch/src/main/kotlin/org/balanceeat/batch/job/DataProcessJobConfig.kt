package org.balanceeat.batch.job

import jakarta.persistence.EntityManagerFactory
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class DataProcessJobConfig(
    private val jobRepository: JobRepository,
private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory
) {
    private val log = LoggerFactory.getLogger(DataProcessJobConfig::class.java)

    companion object {
        private const val CHUNK_SIZE = 10
    }

    @Bean
    fun dataProcessJob(): Job {
        return JobBuilder("dataProcessJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(chunkProcessStep(null))
            .build()
    }

    @Bean
    @JobScope
    fun chunkProcessStep(
        @Value("#{jobParameters['targetDate']}") targetDate: String?
    ): Step {
        return StepBuilder("chunkProcessStep", jobRepository)
            .chunk<ProcessData, ProcessResult>(CHUNK_SIZE, transactionManager)
            .reader(dataReader())
            .processor(dataProcessor(targetDate))
            .writer(dataWriter())
            .build()
    }

    @Bean
    fun dataReader(): JpaPagingItemReader<ProcessData> {
        return JpaPagingItemReaderBuilder<ProcessData>()
            .name("dataReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("""
                SELECT new org.balanceeat.batch.job.ProcessData(
                    1L,
                    'Sample Data',
                    CURRENT_TIMESTAMP
                )
                FROM User u
                WHERE u.id > 0
            """.trimIndent())
            .pageSize(CHUNK_SIZE)
            .build()
    }

    @Bean
    @JobScope
    fun dataProcessor(
        @Value("#{jobParameters['targetDate']}") targetDate: String?
    ): ItemProcessor<ProcessData, ProcessResult> {
        return ItemProcessor { item ->
            log.debug("Processing item: ${item.id}, name: ${item.name}")

            ProcessResult(
                id = item.id,
                processedName = "${item.name} (처리완료)",
                targetDate = targetDate ?: "미지정",
                status = "SUCCESS"
            )
        }
    }

    @Bean
    fun dataWriter(): ItemWriter<ProcessResult> {
        return ItemWriter { items ->
            items.forEach { item ->
                log.info("=== 처리 결과 ===")
                log.info("ID: ${item.id}")
                log.info("처리된 이름: ${item.processedName}")
                log.info("대상 날짜: ${item.targetDate}")
                log.info("상태: ${item.status}")
            }

            log.info("총 ${items.size()}개 항목 처리 완료")
        }
    }
}

data class ProcessData(
    val id: Long,
    val name: String,
    val createdAt: java.time.LocalDateTime
)

data class ProcessResult(
    val id: Long,
    val processedName: String,
    val targetDate: String,
    val status: String
)