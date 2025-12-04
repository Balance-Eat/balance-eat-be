package org.balanceeat.batch.job

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@Disabled("Integration test - Run manually with local DB")
@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
class SampleJobConfigTest {

    @Autowired
    private lateinit var sampleJob: Job

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Test
    @DisplayName("Sample Job이 정상적으로 실행되고 완료된다")
    fun testSampleJobExecution() {
        jobLauncherTestUtils.job = sampleJob

        val jobParameters = JobParametersBuilder()
            .addString("message", "테스트 실행 메시지")
            .addLocalDateTime("requestTime", LocalDateTime.now())
            .toJobParameters()

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(jobExecution.exitStatus).isEqualTo(ExitStatus.COMPLETED)
    }

    @Test
    @DisplayName("Tasklet Step이 정상적으로 실행된다")
    fun testTaskletStepExecution() {
        jobLauncherTestUtils.job = sampleJob

        val jobParameters = JobParametersBuilder()
            .addString("message", "Step 테스트")
            .addLocalDateTime("requestTime", LocalDateTime.now())
            .toJobParameters()

        val jobExecution = jobLauncherTestUtils.launchStep("taskletStep", jobParameters)
        val stepExecution = jobExecution.stepExecutions.first()

        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(stepExecution.readCount).isEqualTo(1)
        assertThat(stepExecution.writeCount).isEqualTo(1)
    }

    @Test
    @DisplayName("Logging Step이 정상적으로 실행된다")
    fun testLoggingStepExecution() {
        jobLauncherTestUtils.job = sampleJob

        val jobExecution = jobLauncherTestUtils.launchStep("loggingStep")

        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
    }
}