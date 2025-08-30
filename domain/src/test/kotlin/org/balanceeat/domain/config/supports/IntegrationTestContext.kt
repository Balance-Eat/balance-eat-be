package org.balanceeat.domain.config.supports

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.balanceeat.domain.config.supports.CleanUp
import org.balanceeat.domain.config.PostgreSQLTestContainerConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Consumer

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
@Import(PostgreSQLTestContainerConfig::class, CleanUp::class)
@Testcontainers
abstract class IntegrationTestContext {
    @Autowired
    private lateinit var cleanUp: CleanUp

    @Autowired
    private lateinit var entityManagerFactory: EntityManagerFactory

    @AfterEach
    fun tearDown() {
        cleanUp.all()
    }

    companion object {
        private val PostgreContainer: JdbcDatabaseContainer<*> = PostgreSQLTestContainerConfig.getContainer()

        @JvmStatic
        @DynamicPropertySource
        fun setDatasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { PostgreContainer.jdbcUrl }
            registry.add("spring.datasource.username") { PostgreContainer.username }
            registry.add("spring.datasource.password") { PostgreContainer.password }
        }
    }

    protected fun runConcurrent(threadCount: Int, task: Runnable) {
        val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        repeat(threadCount) {
            executor.submit {
                try {
                    task.run()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()
    }

    protected fun runConcurrent(threadCount: Int, indexedTask: (Int) -> Unit) {
        val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        repeat(threadCount) { index ->
            executor.submit {
                try {
                    indexedTask(index)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()
    }

    protected fun withManualSession(entityManagerConsumer: Consumer<EntityManager>) {
        entityManagerFactory.createEntityManager().use { em ->
            val tx = em.transaction
            tx.begin()
            entityManagerConsumer.accept(em)
            tx.commit()
        }
    }
}