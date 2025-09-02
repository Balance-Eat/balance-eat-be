package org.balanceeat.apibase

import org.balanceeat.common.Status

enum class ApplicationStatus(override val message: String): Status {
    FOOD_NOT_FOUND("존재하지 않는 음식입니다."),
    CANNOT_MODIFY_FOOD("음식을 수정할 권한이 없습니다.")
}