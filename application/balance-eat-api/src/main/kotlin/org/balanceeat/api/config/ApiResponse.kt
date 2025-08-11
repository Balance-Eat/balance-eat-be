package org.balanceeat.api.config

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: ErrorDetail? = null,
    val timestamp: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun <T> success(
            data: T,
            message: String? = null,
        ): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data,
                message = message,
            )
        }

        fun <T> success(message: String? = "Success"): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = message,
            )
        }

        fun <T> error(
            message: String,
            errorCode: String? = null,
            details: String? = null,
        ): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                error =
                    ErrorDetail(
                        code = errorCode,
                        details = details,
                    ),
            )
        }

        fun <T> error(error: ErrorDetail): ApiResponse<T> {
            return ApiResponse(
                success = false,
                error = error,
                message = error.message,
            )
        }
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorDetail(
    val code: String? = null,
    val message: String? = null,
    val details: String? = null,
    val field: String? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean,
)
