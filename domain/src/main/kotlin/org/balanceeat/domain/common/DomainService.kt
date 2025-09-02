package org.balanceeat.domain.common

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

/**
 * 도메인 서비스임을 나타내는 어노테이션
 * 도메인 계층의 서비스임을 명시적으로 표현
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class DomainService(
    @get:AliasFor(annotation = Component::class)
    val value: String = ""
)