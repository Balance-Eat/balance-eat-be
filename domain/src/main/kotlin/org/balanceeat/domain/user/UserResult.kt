package org.balanceeat.domain.user

import org.balanceeat.domain.common.EntityMapper
import java.time.LocalDateTime

data class UserResult(
    val id: Long,
    val uuid: String,
    val name: String,
    val email: String?,
    val gender: User.Gender,
    val age: Int,
    val weight: Double,
    val height: Double,
    val goalType: User.GoalType,
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
    companion object: EntityMapper<User, UserResult> {
        override fun from(entity: User): UserResult {
            return UserResult(
                id = entity.id,
                uuid = entity.uuid,
                name = entity.name,
                email = entity.email,
                gender = entity.gender,
                age = entity.age,
                weight = entity.weight,
                height = entity.height,
                goalType = entity.goalType,
                activityLevel = entity.activityLevel,
                smi = entity.smi,
                fatPercentage = entity.fatPercentage,
                targetWeight = entity.targetWeight,
                targetCalorie = entity.targetCalorie,
                targetSmi = entity.targetSmi,
                targetFatPercentage = entity.targetFatPercentage,
                targetCarbohydrates = entity.targetCarbohydrates,
                targetProtein = entity.targetProtein,
                targetFat = entity.targetFat,
                providerId = entity.providerId,
                providerType = entity.providerType,
                createdAt = entity.createdAt
            )
        }
    }
}