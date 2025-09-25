package org.balanceeat.domain.common

import org.balanceeat.common.Status

enum class DomainStatus(
    override val message: String
) : Status {
    // common
    INVALID_REQUEST("잘못된 요청입니다"),
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다"),

    // user
    USER_NOT_FOUND("사용자를 찾을 수 없습니다"),
    USER_ALREADY_EXISTS("이미 존재하는 사용자입니다"),

    // diet
    DIET_NOT_FOUND("식단을 찾을 수 없습니다"),
    DIET_FOOD_NOT_FOUND("식단 음식을 찾을 수 없습니다"),
    DIET_MEAL_TYPE_ALREADY_EXISTS("해당 날짜에 이미 같은 식사 유형이 존재합니다"),

    // food
    FOOD_NOT_FOUND("음식을 찾을 수 없습니다"),
    CANNOT_MODIFY_FOOD("음식을 수정할 권한이 없습니다"),
    ;
}