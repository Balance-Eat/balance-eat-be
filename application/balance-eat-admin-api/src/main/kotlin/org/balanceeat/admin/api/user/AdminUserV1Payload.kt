package org.balanceeat.admin.api.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import org.balanceeat.domain.user.User
import org.balanceeat.domain.user.UserDto

class AdminUserV1Request {
    @Schema(name = "AdminUserUpdateRequest", description = "어드민 유저 수정 요청")
    data class Update(
        @field:Size(min = 1, max = 100, message = "이름은 1자 이상 100자 이하여야 합니다")
        @Schema(
            description = "이름",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val name: String? = null,

        @field:Email(message = "유효한 이메일 형식이 아닙니다")
        @field:Size(max = 255, message = "이메일은 255자를 초과할 수 없습니다")
        @Schema(
            description = "이메일",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val email: String? = null,

        @Schema(
            description = "성별",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val gender: User.Gender? = null,

        @field:Min(value = 1, message = "나이는 1세 이상이어야 합니다")
        @field:Max(value = 150, message = "나이는 150세 이하여야 합니다")
        @Schema(
            description = "나이",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val age: Int? = null,

        @field:DecimalMin(value = "10.0", message = "키는 10cm 이상이어야 합니다")
        @field:DecimalMax(value = "300.0", message = "키는 300cm 이하여야 합니다")
        @Schema(
            description = "키 (cm)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val height: Double? = null,

        @field:DecimalMin(value = "10.0", message = "몸무게는 10kg 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "몸무게는 1000kg 이하여야 합니다")
        @Schema(
            description = "몸무게 (kg)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val weight: Double? = null,

        @Schema(
            description = "활동 수준",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val activityLevel: User.ActivityLevel? = null,

        @field:DecimalMin(value = "0.0", message = "SMI는 0 이상이어야 합니다")
        @field:DecimalMax(value = "50.0", message = "SMI는 50 이하여야 합니다")
        @Schema(
            description = "SMI",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val smi: Double? = null,

        @field:DecimalMin(value = "0.0", message = "체지방률은 0% 이상이어야 합니다")
        @field:DecimalMax(value = "100.0", message = "체지방률은 100% 이하여야 합니다")
        @Schema(
            description = "체지방률 (%)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val fatPercentage: Double? = null,

        @field:DecimalMin(value = "10.0", message = "목표 몸무게는 10kg 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "목표 몸무게는 1000kg 이하여야 합니다")
        @Schema(
            description = "목표 몸무게 (kg)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetWeight: Double? = null,

        @field:Min(value = 500, message = "목표 칼로리는 500kcal 이상이어야 합니다")
        @field:Max(value = 10000, message = "목표 칼로리는 10000kcal 이하여야 합니다")
        @Schema(
            description = "목표 칼로리 (kcal)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetCalorie: Int? = null,

        @field:DecimalMin(value = "0.0", message = "목표 SMI는 0 이상이어야 합니다")
        @field:DecimalMax(value = "50.0", message = "목표 SMI는 50 이하여야 합니다")
        @Schema(
            description = "목표 SMI",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetSmi: Double? = null,

        @field:DecimalMin(value = "0.0", message = "목표 체지방률은 0% 이상이어야 합니다")
        @field:DecimalMax(value = "100.0", message = "목표 체지방률은 100% 이하여야 합니다")
        @Schema(
            description = "목표 체지방률 (%)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetFatPercentage: Double? = null,

        @field:DecimalMin(value = "0.0", message = "목표 탄수화물은 0g 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "목표 탄수화물은 1000g 이하여야 합니다")
        @Schema(
            description = "목표 탄수화물 (g)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetCarbohydrates: Double? = null,

        @field:DecimalMin(value = "0.0", message = "목표 단백질은 0g 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "목표 단백질은 1000g 이하여야 합니다")
        @Schema(
            description = "목표 단백질 (g)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetProtein: Double? = null,

        @field:DecimalMin(value = "0.0", message = "목표 지방은 0g 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "목표 지방은 1000g 이하여야 합니다")
        @Schema(
            description = "목표 지방 (g)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetFat: Double? = null,

        @field:Size(max = 255, message = "Provider ID는 255자를 초과할 수 없습니다")
        @Schema(
            description = "Provider ID",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val providerId: String? = null,

        @field:Size(max = 50, message = "Provider Type은 50자를 초과할 수 없습니다")
        @Schema(
            description = "Provider Type",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val providerType: String? = null
    )
}

class AdminUserV1Response {
    @Schema(name = "AdminUserDetailsResponse", description = "어드민 유저 상세정보 응답")
    data class Info(
        val id: Long,
        val uuid: String,
        val name: String,
        val email: String?,
        val gender: User.Gender,
        val age: Int,
        val height: Double,
        val weight: Double,
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