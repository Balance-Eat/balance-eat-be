package org.balanceeat.domain.user

import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val uuid: String,
    val gender: User.Gender,
    val age: Int,
    val weight: Double,
    val height: Double,
    val activityLevel: User.ActivityLevel?,
    val smi: Double?,
    val fatPercentage: Double?,
    val targetWeight: Double?,
    val targetCalorie: Int?,
    val targetSmi: Double?,
    val targetFatPercentage: Double?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(user: User): UserDto {
            return UserDto(
                id = user.id,
                uuid = user.uuid,
                gender = user.gender,
                age = user.age,
                weight = user.weight,
                height = user.height,
                activityLevel = user.activityLevel,
                smi = user.smi,
                fatPercentage = user.fatPercentage,
                targetWeight = user.targetWeight,
                targetCalorie = user.targetCalorie,
                targetSmi = user.targetSmi,
                targetFatPercentage = user.targetFatPercentage,
                createdAt = user.createdAt
            )
        }
    }
}