package org.balanceeat.api.config.supports

import io.restassured.http.Header
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.headers.RequestHeadersSnippet
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.request.ParameterDescriptor
import java.util.*
import java.util.stream.Collectors


@ExtendWith(RestDocumentationExtension::class)
abstract class ControllerTestContext {

    protected fun identifier(): String {
        return javaClass.getSimpleName()
    }

    protected fun identifier(affix: String): String {
        return if (affix.isBlank()) identifier() else "${identifier()}-${affix}"
    }

    protected fun authorizationHeader(): Header {
        return Header(HttpHeaders.AUTHORIZATION, "Bearer ...")
    }

    protected fun requestHeaderWithAuthorization(): RequestHeadersSnippet {
        return requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer JWT_TOKEN")
        )
    }

    protected fun <T : Enum<*>?> enumDescription(
        descriptor: ParameterDescriptor,
        enumClass: Class<T?>
    ): ParameterDescriptor {
        return descriptor.description(
            "%s : %s".format(
                descriptor.description,
                Arrays.stream<T?>(enumClass.getEnumConstants())
                    .map<String?> { obj: T? -> obj!!.name }
                    .collect(Collectors.joining(", "))
            )
        )
    }

    protected fun <T : Enum<*>?> enumDescription(
        descriptor: FieldDescriptor,
        enumClass: Class<T?>
    ): FieldDescriptor {
        return descriptor.description(
            "%s : %s".format(
                descriptor.description,
                Arrays.stream<T?>(enumClass.getEnumConstants())
                    .map<String?> { obj: T? -> obj!!.name }
                    .collect(Collectors.joining(", "))
            )
        )
    }

    protected enum class Tags(val tagName: String) {
        POINT("포인트"),
        PRODUCT("상품"),
        ORDER("주문"),
        COUPON("쿠폰"),
        ;

        fun tagName(): String {
            return tagName
        }

        fun descriptionWith(affix: String): String {
            return "%s : %s".format(tagName, affix)
        }
    }
}