package org.balanceeat.apibase

import org.balanceeat.common.Status

enum class ApplicationStatus(override val message: String): Status {
    INVALID_PARAMETER("잘못된 요청 파라미터입니다."),

    // user
    USER_NOT_FOUND("존재하지 않는 사용자입니다."),

    // food
    FOOD_NOT_FOUND("존재하지 않는 음식입니다."),
    CANNOT_MODIFY_FOOD("음식을 수정할 권한이 없습니다."),

    // diet
    DIET_NOT_FOUND("존재하지 않는 식단입니다."),
    CANNOT_MODIFY_DIET("식단을 수정할 권한이 없습니다,"),

    // notification_device
    NOTIFICATION_DEVICE_NOT_FOUND("존재하지 않는 디바이스입니다."),
    NOTIFICATION_DEVICE_UNAUTHORIZED("디바이스를 수정할 권한이 없습니다."),

    // reminder
    REMINDER_NOT_FOUND("존재하지 않는 리마인더입니다."),
}