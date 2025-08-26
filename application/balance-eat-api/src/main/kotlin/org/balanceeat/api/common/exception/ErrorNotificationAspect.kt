package org.balanceeat.api.common.exception

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Aspect
@Component
class ErrorNotificationAspect(
    private val errorNotificationSender: ErrorNotificationSender
) {
    private val log = LoggerFactory.getLogger(ErrorNotificationAspect::class.java)

    @AfterThrowing(
        pointcut = "within(@org.springframework.web.bind.annotation.RestController *)",
        throwing = "exception"
    )
    fun writeFailLog(joinPoint: JoinPoint, exception: Exception) {
        val className = joinPoint.target.javaClass.simpleName
        val methodName = joinPoint.signature.name
        val argsString = joinPoint.args.contentToString()

        val errorMessage = """
            ${className}.${methodName} Failure - Args: $argsString
            queryParams: ${MDC.get("queryParams")}
            requestBody: ${MDC.get("requestBody")}
        """.trimIndent()
        errorNotificationSender.send(errorMessage, exception)
        log.warn(errorMessage)
    }
}
