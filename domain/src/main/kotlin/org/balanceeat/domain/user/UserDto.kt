package org.balanceeat.domain.user

import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val uuid: String,
    val name: String,
    val email: String?,
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
    val targetCarbohydrates: Double?,
    val targetProtein: Double?,
    val targetFat: Double?,
    val providerId: String?,
    val providerType: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(user: User): UserDto {
            return UserDto(
                id = user.id,
                uuid = user.uuid,
                name = user.name,
                email = user.email,
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
                targetCarbohydrates = user.targetCarbohydrates,
                targetProtein = user.targetProtein,
                targetFat = user.targetFat,
                providerId = user.providerId,
                providerType = user.providerType,
                createdAt = user.createdAt
            )
        }
    }
}