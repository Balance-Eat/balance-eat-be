package org.balanceeat.apibase.response

import org.balanceeat.common.Status
import java.time.LocalDateTime

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T? = null,
    val serverDatetime: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun <T> success(data: T? = null): ApiResponse<T> {
            return ApiResponse(
                status = ApiResponseStatus.SUCCESS.name,
                message = ApiResponseStatus.SUCCESS.message,
                data = data
            )
        }

        fun <T> success(data: T, message: String): ApiResponse<T> {
            return ApiResponse(
                status = ApiResponseStatus.SUCCESS.name,
                message = message,
                data = data
            )
        }

        fun <T> fail(status: Status, message: String?, data: T? = null): ApiResponse<T> {
            return ApiResponse(
                status = status.name,
                message = message ?: status.message,
                data = data
            )
        }

        fun <T> fail(message: String): ApiResponse<T> {
            return ApiResponse(
                status = ApiResponseStatus.FAILURE.name,
                message = message,
                data = null
            )
        }

        fun <T> error(message: String, data: T? = null): ApiResponse<T> {
            return ApiResponse(
                status = ApiResponseStatus.ERROR.name,
                message = message,
                data = data
            )
        }

        fun <T> error(status: Status, message: String): ApiResponse<T> {
            return ApiResponse(
                status = status.name,
                message = message,
                data = null
            )
        }

        fun <T> unauthorized(status: Status = ApiResponseStatus.UNAUTHORIZED, message: String? = null): ApiResponse<T> {
            return ApiResponse(
                status = status.name,
                message = message ?: status.message,
                data = null
            )
        }

        fun <T> notFound(status: Status = ApiResponseStatus.NOT_FOUND, message: String? = null): ApiResponse<T> {
            return ApiResponse(
                status = status.name,
                message = message ?: status.message,
                data = null
            )
        }

        fun <T> forbidden(): ApiResponse<T> {
            return ApiResponse(
                status = ApiResponseStatus.FORBIDDEN.name,
                message = ApiResponseStatus.FORBIDDEN.message,
                data = null
            )
        }
    }
    
    enum class ApiResponseStatus(override val message: String) : Status {
        SUCCESS("성공"),
        FAILURE("요청에 실패하였습니다."),
        ERROR("에러가 발생하였습니다."),
        UNAUTHORIZED("토큰이 유효하지 않습니다."),
        FORBIDDEN("권한이 없습니다."),
        NOT_FOUND("해당 리소스는 존재하지 않습니다.");
    }
}

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean,
)