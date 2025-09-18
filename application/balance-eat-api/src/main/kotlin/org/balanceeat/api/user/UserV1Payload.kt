package org.balanceeat.api.user

import jakarta.validation.constraints.NotNull
import org.balanceeat.domain.user.User

class UserV1Request {
    data class Create(
        @field:NotNull(message = "UUID는 필수입니다")
        val uuid: String,
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
        val targetFat: Double? = null,
        val providerId: String? = null,
        val providerType: String? = null
    )

    data class Update(
        val name: String? = null,
        val email: String? = null,
        val gender: User.Gender? = null,
        val age: Int? = null,
        val height: Double? = null,
        val weight: Double? = null,
        val goalType: User.GoalType? = null,
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

class UserV1Response {
    data class Info(
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
        val providerType: String?
    )
}