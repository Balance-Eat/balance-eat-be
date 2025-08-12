package org.balanceeat.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    fun findByUuid(uuid: String): User?

    fun existsByEmail(email: String): Boolean

    fun findByNameContaining(name: String): List<User>
}
