package org.balanceeat.api.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.balanceeat.domain.user.User
import java.math.BigDecimal

class UserV1Request {
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
        val height: BigDecimal,
        @field:NotNull(message = "weight은 필수입니다")
        @Schema(
            description = "사용자 몸무게 (kg 단위)",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val weight: BigDecimal,
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
        val smi: BigDecimal? = null,
        @Schema(
            description = "사용자 체지방률 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val fatPercentage: BigDecimal? = null,
        @Schema(
            description = "사용자 목표 체중 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetWeight: BigDecimal? = null,
        @Schema(
            description = "사용자 목표 칼로리 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetCalorie: Int? = null,
        @Schema(
            description = "사용자 목표 SMI (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetSmi: BigDecimal? = null,
        @Schema(
            description = "사용자 목표 체지방률 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetFatPercentage: BigDecimal? = null,
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
        val height: BigDecimal? = null,
        @Schema(
            description = "사용자 몸무게 (kg 단위) (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val weight: BigDecimal? = null,
        @Schema(
            description = "사용자 활동 수준 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val activityLevel: User.ActivityLevel? = null,
        @Schema(
            description = "사용자 SMI (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val smi: BigDecimal? = null,
        @Schema(
            description = "사용자 체지방률 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val fatPercentage: BigDecimal? = null,
        @Schema(
            description = "사용자 목표 체중 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetWeight: BigDecimal? = null,
        @Schema(
            description = "사용자 목표 칼로리 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetCalorie: Int? = null,
        @Schema(
            description = "사용자 목표 SMI (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetSmi: BigDecimal? = null,
        @Schema(
            description = "사용자 목표 체지방률 (선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val targetFatPercentage: BigDecimal? = null
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
        val weight: BigDecimal,
        val height: BigDecimal,
        val activityLevel: User.ActivityLevel?,
        val smi: BigDecimal?,
        val fatPercentage: BigDecimal?,
        val targetWeight: BigDecimal?,
        val targetCalorie: Int?,
        val targetSmi: BigDecimal?,
        val targetFatPercentage: BigDecimal?,
        val providerId: String?,
        val providerType: String?
    )
}
