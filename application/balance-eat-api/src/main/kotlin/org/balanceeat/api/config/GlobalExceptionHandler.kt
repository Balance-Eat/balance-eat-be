package org.balanceeat.api.config

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import java.time.format.DateTimeParseException

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Any>> {
        val errors =
            ex.bindingResult.fieldErrors.map { error ->
                ErrorDetail(
                    code = "VALIDATION_FAILED",
                    field = error.field,
                    message = error.defaultMessage,
                    details = "Rejected value: ${error.rejectedValue}",
                )
            }

        logger.warn { "Validation failed: ${errors.joinToString { "${it.field}: ${it.message}" }}" }

        return ResponseEntity.badRequest().body(
            ApiResponse.error<Any>(
                message = "입력값 검증에 실패했습니다",
                errorCode = "VALIDATION_FAILED",
                details = errors.joinToString(", ") { "${it.field}: ${it.message}" },
            ),
        )
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(ex: BindException): ResponseEntity<ApiResponse<Any>> {
        val errors =
            ex.fieldErrors.map { error ->
                "${error.field}: ${error.defaultMessage}"
            }

        logger.warn { "Bind exception: ${errors.joinToString()}" }

        return ResponseEntity.badRequest().body(
            ApiResponse.error<Any>(
                message = "요청 데이터 바인딩에 실패했습니다",
                errorCode = "BIND_FAILED",
                details = errors.joinToString(", "),
            ),
        )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParams(ex: MissingServletRequestParameterException): ResponseEntity<ApiResponse<Any>> {
        logger.warn { "Missing parameter: ${ex.parameterName}" }

        return ResponseEntity.badRequest().body(
            ApiResponse.error<Any>(
                message = "필수 파라미터가 누락되었습니다: ${ex.parameterName}",
                errorCode = "MISSING_PARAMETER",
            ),
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Any>> {
        logger.warn { "Type mismatch: ${ex.name} = ${ex.value}" }

        return ResponseEntity.badRequest().body(
            ApiResponse.error<Any>(
                message = "파라미터 타입이 올바르지 않습니다: ${ex.name}",
                errorCode = "TYPE_MISMATCH",
                details = "Expected: ${ex.requiredType?.simpleName}, Actual: ${ex.value}",
            ),
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Any>> {
        logger.warn { "HTTP message not readable: ${ex.message}" }

        return ResponseEntity.badRequest().body(
            ApiResponse.error<Any>(
                message = "요청 본문을 읽을 수 없습니다",
                errorCode = "MESSAGE_NOT_READABLE",
            ),
        )
    }

    @ExceptionHandler(DateTimeParseException::class)
    fun handleDateTimeParseException(ex: DateTimeParseException): ResponseEntity<ApiResponse<Any>> {
        logger.warn { "DateTime parse error: ${ex.message}" }

        return ResponseEntity.badRequest().body(
            ApiResponse.error<Any>(
                message = "날짜 형식이 올바르지 않습니다",
                errorCode = "DATETIME_PARSE_ERROR",
                details = ex.parsedString,
            ),
        )
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<Any>> {
        logger.warn { "Method not supported: ${ex.method}" }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
            ApiResponse.error<Any>(
                message = "지원하지 않는 HTTP 메서드입니다: ${ex.method}",
                errorCode = "METHOD_NOT_ALLOWED",
                details = "Supported methods: ${ex.supportedMethods?.joinToString()}",
            ),
        )
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFound(ex: NoHandlerFoundException): ResponseEntity<ApiResponse<Any>> {
        logger.warn { "No handler found: ${ex.httpMethod} ${ex.requestURL}" }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse.error<Any>(
                message = "요청한 리소스를 찾을 수 없습니다",
                errorCode = "NOT_FOUND",
                details = "${ex.httpMethod} ${ex.requestURL}",
            ),
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Any>> {
        logger.warn { "Illegal argument: ${ex.message}" }

        return ResponseEntity.badRequest().body(
            ApiResponse.error<Any>(
                message = "잘못된 요청입니다",
                errorCode = "ILLEGAL_ARGUMENT",
                details = ex.message,
            ),
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse<Any>> {
        logger.error(ex) { "Unexpected error occurred" }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse.error<Any>(
                message = "서버 내부 오류가 발생했습니다",
                errorCode = "INTERNAL_SERVER_ERROR",
                details = if (logger.isDebugEnabled) ex.message else null,
            ),
        )
    }
}
