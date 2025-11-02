package org.balanceeat.api.config.supports

import jakarta.persistence.EntityManager
import org.balanceeat.domain.config.DatabaseCleanUp
import org.balanceeat.domain.config.PostgreSQLTestContainerConfig
import org.hibernate.Session
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.balanceeat.domain.config.BaseEntity
import java.time.LocalDateTime
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.junit.jupiter.Testcontainers
import jakarta.persistence.criteria.CriteriaQuery

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(PostgreSQLTestContainerConfig::class, DatabaseCleanUp::class, MockMvcConfig::class)
@Testcontainers
abstract class IntegrationTestContext {
    @Autowired
    private lateinit var databaseCleanUp: DatabaseCleanUp
    @Autowired
     protected lateinit var mockMvc: MockMvc
    @Autowired
    protected lateinit var entityManager: EntityManager

    @AfterEach
    fun tearDown() {
        databaseCleanUp.all()
    }

    protected fun <T> createEntity(entity: T, withTransaction: Boolean = false): T {
        // BaseEntity의 경우 시간 필드를 수동으로 설정
        if (entity is BaseEntity) {
            val now = LocalDateTime.now()
            entity.createdAt = now
            entity.updatedAt = now
        }

        if (withTransaction) {
            entityManager.persist(entity)
        } else {
            val session = entityManager.unwrap<Session>(Session::class.java)
            val statelessSession = session.sessionFactory.openStatelessSession()
            val transaction = statelessSession.beginTransaction()

            statelessSession.insert(entity)
            transaction.commit()
            statelessSession.close()
        }

        return entity
    }

    protected fun <T> getAllEntities(entityClass: Class<T>): List<T> {
        val builder = entityManager.criteriaBuilder
        val query: CriteriaQuery<T> = builder.createQuery(entityClass)
        val root = query.from(entityClass)
        query.select(root)
        return entityManager.createQuery(query).resultList
    }
}

@TestConfiguration
class MockMvcConfig {
    @Bean
    fun customMockMvc(webApplicationContext: WebApplicationContext): MockMvc {
        return MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .build()
    }
}