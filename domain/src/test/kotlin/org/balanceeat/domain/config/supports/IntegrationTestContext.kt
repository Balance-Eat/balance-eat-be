package org.balanceeat.domain.config.supports

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.DatabaseCleanUp
import org.balanceeat.domain.config.PostgreSQLTestContainerConfig
import org.hibernate.Session
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Consumer

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
@Import(PostgreSQLTestContainerConfig::class, DatabaseCleanUp::class)
@Testcontainers
abstract class IntegrationTestContext {
    @Autowired
    private lateinit var databaseCleanUp: DatabaseCleanUp

    @Autowired
    protected lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var entityManagerFactory: EntityManagerFactory

    @AfterEach
    fun tearDown() {
        databaseCleanUp.all()
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

    protected fun <T> createEntity(entity: T): T {
        // BaseEntity의 경우 시간 필드를 수동으로 설정
        if (entity is BaseEntity) {
            val now = LocalDateTime.now()
            entity.createdAt = now
            entity.updatedAt = now
        }

        val session = entityManager.unwrap<Session>(Session::class.java)
        val statelessSession = session.sessionFactory.openStatelessSession()
        val transaction = statelessSession.beginTransaction()

        statelessSession.insert(entity)
        transaction.commit()
        statelessSession.close()

        return entity
    }
}