package org.balanceeat.api.config

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val serverDatetime: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun <T> success(
            data: T,
            message: String? = null,
        ): ApiResponse<T> {
            return ApiResponse(
                data = data,
                message = message,
            )
        }

        fun <T> success(message: String? = "Success"): ApiResponse<T> {
            return ApiResponse(
                message = message,
            )
        }

        fun <T> error(
            message: String
        ): ApiResponse<T> {
            return ApiResponse(
                message = message
            )
        }
    }
}

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
