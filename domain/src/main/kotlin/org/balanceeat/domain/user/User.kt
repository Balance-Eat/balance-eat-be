package org.balanceeat.domain.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "\"user\"")
@EntityListeners(AuditingEntityListener::class)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @Column(nullable = false, unique = true)
    val uuid: UUID = UUID.randomUUID(),
    @Column(nullable = false, length = 100)
    val name: String,
    @Column(nullable = false, unique = true, length = 255)
    val email: String,
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    val gender: Gender? = null,
    @Column
    val age: Int? = null,
    @Column(precision = 5, scale = 2)
    val weight: BigDecimal? = null,
    @Column(precision = 5, scale = 2)
    val height: BigDecimal? = null,
    @Column(precision = 5, scale = 2)
    val smi: BigDecimal? = null,
    @Column(name = "fat_percentage", precision = 5, scale = 2)
    val fatPercentage: BigDecimal? = null,
    @Column(name = "target_weight", precision = 5, scale = 2)
    val targetWeight: BigDecimal? = null,
    @Column(name = "target_calorie")
    val targetCalorie: Int? = null,
    @Column(name = "target_smi", precision = 5, scale = 2)
    val targetSmi: BigDecimal? = null,
    @Column(name = "target_fat_percentage", precision = 5, scale = 2)
    val targetFatPercentage: BigDecimal? = null,
    @Column(name = "provider_id", length = 255)
    val providerId: String? = null,
    @Column(name = "provider_type", length = 50)
    val providerType: String? = null,
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    // JPA를 위한 기본 생성자
    constructor() : this(
        name = "",
        email = "",
    )
}

enum class Gender {
    MALE,
    FEMALE,
    OTHER,
}
