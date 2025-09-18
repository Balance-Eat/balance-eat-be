package org.balanceeat.admin.api.user

import jakarta.validation.constraints.*
import org.balanceeat.domain.user.User
import org.balanceeat.domain.user.UserDto

class AdminUserV1Request {
    data class Update(
        @field:Size(min = 1, max = 100, message = "이름은 1자 이상 100자 이하여야 합니다")
        val name: String? = null,

        @field:Email(message = "유효한 이메일 형식이 아닙니다")
        @field:Size(max = 255, message = "이메일은 255자를 초과할 수 없습니다")
        val email: String? = null,

        val gender: User.Gender? = null,

        @field:Min(value = 1, message = "나이는 1세 이상이어야 합니다")
        @field:Max(value = 150, message = "나이는 150세 이하여야 합니다")
        val age: Int? = null,

        @field:DecimalMin(value = "10.0", message = "키는 10cm 이상이어야 합니다")
        @field:DecimalMax(value = "300.0", message = "키는 300cm 이하여야 합니다")
        val height: Double? = null,

        @field:DecimalMin(value = "10.0", message = "몸무게는 10kg 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "몸무게는 1000kg 이하여야 합니다")
        val weight: Double? = null,

        val goalType: User.GoalType? = null,

        val activityLevel: User.ActivityLevel? = null,

        @field:DecimalMin(value = "0.0", message = "SMI는 0 이상이어야 합니다")
        @field:DecimalMax(value = "50.0", message = "SMI는 50 이하여야 합니다")
        val smi: Double? = null,

        @field:DecimalMin(value = "0.0", message = "체지방률은 0% 이상이어야 합니다")
        @field:DecimalMax(value = "100.0", message = "체지방률은 100% 이하여야 합니다")
        val fatPercentage: Double? = null,

        @field:DecimalMin(value = "10.0", message = "목표 몸무게는 10kg 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "목표 몸무게는 1000kg 이하여야 합니다")
        val targetWeight: Double? = null,

        @field:Min(value = 500, message = "목표 칼로리는 500kcal 이상이어야 합니다")
        @field:Max(value = 10000, message = "목표 칼로리는 10000kcal 이하여야 합니다")
        val targetCalorie: Int? = null,

        @field:DecimalMin(value = "0.0", message = "목표 SMI는 0 이상이어야 합니다")
        @field:DecimalMax(value = "50.0", message = "목표 SMI는 50 이하여야 합니다")
        val targetSmi: Double? = null,

        @field:DecimalMin(value = "0.0", message = "목표 체지방률은 0% 이상이어야 합니다")
        @field:DecimalMax(value = "100.0", message = "목표 체지방률은 100% 이하여야 합니다")
        val targetFatPercentage: Double? = null,

        @field:DecimalMin(value = "0.0", message = "목표 탄수화물은 0g 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "목표 탄수화물은 1000g 이하여야 합니다")
        val targetCarbohydrates: Double? = null,

        @field:DecimalMin(value = "0.0", message = "목표 단백질은 0g 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "목표 단백질은 1000g 이하여야 합니다")
        val targetProtein: Double? = null,

        @field:DecimalMin(value = "0.0", message = "목표 지방은 0g 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "목표 지방은 1000g 이하여야 합니다")
        val targetFat: Double? = null,

        @field:Size(max = 255, message = "Provider ID는 255자를 초과할 수 없습니다")
        val providerId: String? = null,

        @field:Size(max = 50, message = "Provider Type은 50자를 초과할 수 없습니다")
        val providerType: String? = null
    )
}

class AdminUserV1Response {
    data class Info(
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
            fun from(user: UserDto) = Info(
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