package org.balanceeat.domain.config

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.time.ZonedDateTime

@MappedSuperclass
abstract class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: LocalDateTime

    fun guard() = Unit

    @PrePersist
    private fun prePersist() {
        guard()

        val now = LocalDateTime.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    private fun preUpdate() {
        guard()

        val now = LocalDateTime.now()
        updatedAt = now
    }
}

fun List<BaseEntity>.toIds(): List<Long> = this.map {
        val field = it::class.java.getDeclaredField("id")
        field.isAccessible = true
        field.get(it) as Long
    }
    .toList()

const val NEW_ID = 0L
