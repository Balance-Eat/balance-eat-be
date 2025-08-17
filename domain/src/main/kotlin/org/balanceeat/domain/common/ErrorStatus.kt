package org.balanceeat.domain.common

enum class ErrorStatus(
    override val message: String
) : Status {
    USER_NOT_FOUND("사용자를 찾을 수 없습니다"),
    INVALID_REQUEST("잘못된 요청입니다"),
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다"),
    DIET_NOT_FOUND("식단을 찾을 수 없습니다"),
    DIET_FOOD_NOT_FOUND("식단 음식을 찾을 수 없습니다"),
    FOOD_NOT_FOUND("음식을 찾을 수 없습니다")
}