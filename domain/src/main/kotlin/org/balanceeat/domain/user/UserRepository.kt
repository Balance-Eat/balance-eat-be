package org.balanceeat.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    fun findByUuid(uuid: UUID): User?

    fun existsByEmail(email: String): Boolean

    fun findByNameContaining(name: String): List<User>
}
