package org.balanceeat.api.common.annotation

import org.balanceeat.common.Status
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DetectNotFoundStatus(
    val status: KClass<out Status>
)
