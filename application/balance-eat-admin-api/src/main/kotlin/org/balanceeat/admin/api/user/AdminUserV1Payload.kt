package org.balanceeat.admin.api.user

import jakarta.validation.constraints.*
import org.balanceeat.domain.user.User
import org.balanceeat.domain.user.UserResult

class AdminUserV1Request {
    data class Update(
        @field:NotNull(message = "name은 필수입니다")
        val name: String,
        @field:NotNull(message = "gender은 필수입니다")
        val gender: User.Gender,
        @field:NotNull(message = "age는 필수입니다")
        val age: Int,
        @field:NotNull(message = "height는 필수입니다")
        val height: Double,
        @field:NotNull(message = "weight은 필수입니다")
        val weight: Double,
        @field:NotNull(message = "goalType은 필수입니다")
        val goalType: User.GoalType,
        val email: String? = null,
        val activityLevel: User.ActivityLevel? = null,
        val smi: Double? = null,
        val fatPercentage: Double? = null,
        val targetWeight: Double? = null,
        val targetCalorie: Int? = null,
        val targetSmi: Double? = null,
        val targetFatPercentage: Double? = null,
        val targetCarbohydrates: Double? = null,
        val targetProtein: Double? = null,
        val targetFat: Double? = null
    )
}

class AdminUserV1Response {
    data class Details(
        val id: Long,
        val uuid: String,
        val name: String,
        val email: String?,
        val gender: User.Gender,
        val age: Int,
        val height: Double,
        val weight: Double,
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
        val providerType: String?
    ) {
        companion object {
            fun from(user: UserResult) = Details(
                id = user.id,
                uuid = user.uuid,
                name = user.name,
                email = user.email,
                gender = user.gender,
                age = user.age,
                height = user.height,
                weight = user.weight,
                goalType = user.goalType,
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
                providerType = user.providerType
            )
        }
    }
}