package org.balanceeat.api.dto

import org.balanceeat.domain.user.Gender
import org.balanceeat.domain.user.User
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class UserCreateRequest(
    val name: String,
    val email: String,
    val gender: Gender? = null,
    val age: Int? = null,
    val weight: BigDecimal? = null,
    val height: BigDecimal? = null,
    val targetWeight: BigDecimal? = null,
    val targetCalorie: Int? = null,
)

data class UserResponse(
    val id: Long,
    val uuid: UUID,
    val name: String,
    val email: String,
    val gender: Gender?,
    val age: Int?,
    val weight: BigDecimal?,
    val height: BigDecimal?,
    val targetWeight: BigDecimal?,
    val targetCalorie: Int?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                uuid = user.uuid,
                name = user.name,
                email = user.email,
                gender = user.gender,
                age = user.age,
                weight = user.weight,
                height = user.height,
                targetWeight = user.targetWeight,
                targetCalorie = user.targetCalorie,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
            )
        }
    }
}
