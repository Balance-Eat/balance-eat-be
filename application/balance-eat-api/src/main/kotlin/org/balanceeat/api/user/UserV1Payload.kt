package org.balanceeat.api.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.balanceeat.domain.user.User

class UserV1Request {
    @Schema(name = "UserCreateRequest", description = "사용자 생성 요청")
    data class Create(
        @field:NotNull(message = "UUID는 필수입니다")
        @Schema(
            description = "사용자 UUID",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val uuid: String,
        @field:NotNull(message = "name은 필수입니다")
        @Schema(
            description = "사용자 이름",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val name: String,
        @field:NotNull(message = "gender은 필수입니다")
        @Schema(
            description = "사용자 성별",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val gender: User.Gender,
        @field:NotNull(message = "age는 필수입니다")
        @Schema(
            description = "사용자 나이",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val age: Int,
        @field:NotNull(message = "height는 필수입니다")
        @Schema(
            description = "사용자 키 (cm 단위)",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val height: Double,
        @field:NotNull(message = "weight은 필수입니다")
        @Schema(
            description = "사용자 몸무게 (kg 단위)",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val weight: Double,
        @Schema(
            description = "사용자 이메일 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val email: String? = null,
        @Schema(
            description = "사용자 활동 수준 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val activityLevel: User.ActivityLevel? = null,
        @Schema(
            description = "사용자 SMI (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val smi: Double? = null,
        @Schema(
            description = "사용자 체지방률 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val fatPercentage: Double? = null,
        @Schema(
            description = "사용자 목표 체중 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetWeight: Double? = null,
        @Schema(
            description = "사용자 목표 칼로리 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetCalorie: Int? = null,
        @Schema(
            description = "사용자 목표 SMI (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetSmi: Double? = null,
        @Schema(
            description = "사용자 목표 체지방률 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetFatPercentage: Double? = null,
        @Schema(
            description = "사용자 목표 탄수화물 (g, 선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetCarbohydrates: Double? = null,
        @Schema(
            description = "사용자 목표 단백질 (g, 선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetProtein: Double? = null,
        @Schema(
            description = "사용자 목표 지방 (g, 선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetFat: Double? = null,
        @Schema(
            description = "제공자 ID (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val providerId: String? = null,
        @Schema(
            description = "제공자 유형 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val providerType: String? = null
    )

    @Schema(name = "UserUpdateRequest", description = "사용자 수정 요청")
    data class Update(
        @Schema(
            description = "사용자 이름 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val name: String? = null,
        @Schema(
            description = "사용자 이메일 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val email: String? = null,
        @Schema(
            description = "사용자 성별 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val gender: User.Gender? = null,
        @Schema(
            description = "사용자 나이 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val age: Int? = null,
        @Schema(
            description = "사용자 키 (cm 단위) (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val height: Double? = null,
        @Schema(
            description = "사용자 몸무게 (kg 단위) (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val weight: Double? = null,
        @Schema(
            description = "사용자 활동 수준 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val activityLevel: User.ActivityLevel? = null,
        @Schema(
            description = "사용자 SMI (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val smi: Double? = null,
        @Schema(
            description = "사용자 체지방률 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val fatPercentage: Double? = null,
        @Schema(
            description = "사용자 목표 체중 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetWeight: Double? = null,
        @Schema(
            description = "사용자 목표 칼로리 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetCalorie: Int? = null,
        @Schema(
            description = "사용자 목표 SMI (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetSmi: Double? = null,
        @Schema(
            description = "사용자 목표 체지방률 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetFatPercentage: Double? = null,
        @Schema(
            description = "사용자 목표 탄수화물 (g, 선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetCarbohydrates: Double? = null,
        @Schema(
            description = "사용자 목표 단백질 (g, 선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetProtein: Double? = null,
        @Schema(
            description = "사용자 목표 지방 (g, 선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetFat: Double? = null
    )
}

class UserV1Response {
    @Schema(name = "UserDetailsResponse", description = "사용자 상세정보 응답")
    data class Info(
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
        val providerType: String?
    )
}
