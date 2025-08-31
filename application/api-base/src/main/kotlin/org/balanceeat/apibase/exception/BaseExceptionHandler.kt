package org.balanceeat.apibase.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import mu.KotlinLogging
import org.balanceeat.apibase.response.ApiResponse
import org.balanceeat.domain.common.exception.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.ObjectError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.time.format.DateTimeParseException

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class BaseExceptionHandler {
    companion object {
        const val USER_4XX_MESSAGE = "잘못된 요청입니다."
        const val USER_5XX_MESSAGE = "예상치 못한 오류가 발생하였습니다. 관리자에게 문의해주세요"
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException::class)
    fun handleBindException(ex: BindException, request: HttpServletRequest): ApiResponse<Any> {
        logger.warn(ex) { "[${request.requestURI}] 잘못된 요청이 발생하였습니다." }

        return ApiResponse.Companion.fail(
            ex.bindingResult.allErrors
                .mapNotNull(ObjectError::getDefaultMessage)
                .joinToString("\n")
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException, request: HttpServletRequest): ApiResponse<Any> {
        logger.warn(ex) { "[${request.requestURI}] 잘못된 요청이 발생하였습니다." }
        return ApiResponse.Companion.fail(ex.message ?: USER_4XX_MESSAGE)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        IllegalArgumentException::class,
        IllegalStateException::class,
    )
    fun handleIllegalException(ex: Exception, request: HttpServletRequest): ApiResponse<Any> {
        logger.warn(ex) { "[${request.requestURI}] 잘못된 요청이 발생하였습니다." }
        return ApiResponse.Companion.fail(ex.message ?: USER_4XX_MESSAGE)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        HttpRequestMethodNotSupportedException::class,
        HttpMessageNotReadableException::class,
        MethodArgumentTypeMismatchException::class,
        MissingServletRequestParameterException::class,
        MultipartException::class,
        DateTimeParseException::class,
    )
    fun handle4xx(ex: Exception, request: HttpServletRequest): ApiResponse<Any> {
        logger.warn(ex) { "[${request.requestURI}] 잘못된 요청이 발생하였습니다." }
        return ApiResponse.Companion.fail(USER_4XX_MESSAGE)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handle5xx(ex: Exception, request: HttpServletRequest): ApiResponse<Any> {
        logger.error(ex) { "[${request.requestURI}] 예상치 못한 오류가 발생하였습니다." }
        return ApiResponse.Companion.error(USER_5XX_MESSAGE)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException, request: HttpServletRequest): ApiResponse<Any> {
        logger.warn(ex) { "[${request.requestURI}] 잘못된 요청이 발생하였습니다." }
        return ApiResponse.Companion.fail(ex.status, ex.message)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException::class, NotFoundException::class)
    fun handleNoResourceFoundException(ex: Exception, request: HttpServletRequest): ApiResponse<Any> {
        logger.warn { "[${request.requestURI}] 해당 리소스는 존재하지 않습니다." }
        return ApiResponse.Companion.notFound()
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalErrorException::class)
    fun handleInternalErrorException(ex: InternalErrorException, request: HttpServletRequest): ApiResponse<Any> {
        logger.error(ex) { "[${request.requestURI}] 서버 오류가 발생하였습니다." }
        return ApiResponse.Companion.error(ex.message ?: "서버 오류가 발생했습니다.")
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException, request: HttpServletRequest): ApiResponse<Any> {
        if (ex.isCritical) {
            logger.error(ex) { "[${request.requestURI}] 도메인 오류가 발생하였습니다." }
        } else {
            logger.warn(ex) { "[${request.requestURI}] 도메인 오류가 발생하였습니다." }
        }
        return ApiResponse.Companion.fail(ex.status, ex.message ?: "도메인 오류가 발생했습니다.")
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(ex: UnauthorizedException, request: HttpServletRequest): ApiResponse<Any> {
        logger.warn(ex) { "[${request.requestURI}] 인증 오류가 발생하였습니다." }
        return ApiResponse.Companion.unauthorized(ex.status, ex.message)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, request: HttpServletRequest): ApiResponse<Any> {
        logger.warn(ex) { "[${request.requestURI}] 입력값 검증에 실패했습니다." }
        
        return ApiResponse.Companion.fail(
            ex.bindingResult
                .allErrors
                .mapNotNull(ObjectError::getDefaultMessage)
                .joinToString("\n")
        )
    }
}