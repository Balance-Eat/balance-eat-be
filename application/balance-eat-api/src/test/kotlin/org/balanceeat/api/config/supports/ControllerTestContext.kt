package org.balanceeat.api.config.supports

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippetDetails
import io.restassured.http.ContentType
import io.restassured.http.Header
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.headers.RequestHeadersSnippet
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.reflect.KClass


@ExtendWith(RestDocumentationExtension::class)
abstract class ControllerTestContext {
    protected lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var context: WebApplicationContext

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider?) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
            .build()
    }

    protected fun given(): MockMvcRequestSpecification {
        return RestAssuredMockMvc.given().mockMvc(mockMvc)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON.withCharset(StandardCharsets.UTF_8))
    }

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
        FOOD("음식"),
        USER("사용자"),
        DIET("식단"),
        STATS("통계"),
        NOTIFICATION("알림"),
        REMINDER("리마인더")
        ;

        fun tagName(): String {
            return tagName
        }

        fun descriptionWith(affix: String): String {
            return "%s : %s".format(tagName, affix)
        }
    }

    protected fun document(
        identifier: String,
        resourceDetails: ResourceSnippetDetails,
        vararg snippets: Snippet
    ): RestDocumentationResultHandler {
        return MockMvcRestDocumentationWrapper.document(
            identifier = identifier,
            resourceDetails = resourceDetails,
            requestPreprocessor = Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
            responsePreprocessor = Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
            snippetFilter = Function.identity(),
            *snippets
        )
    }


    protected infix fun String.type(jsonFieldType: JsonFieldType): FieldDescriptor {
        return PayloadDocumentation.fieldWithPath(this).type(jsonFieldType)
    }

    protected infix fun String.pathMeans(value: String): ParameterDescriptor {
        return parameterWithName(this).description(value)
    }

    protected infix fun String.queryMeans(value: String): ParameterDescriptor {
        return parameterWithName(this).description(value)
    }

    protected infix fun FieldDescriptor.means(value: String): FieldDescriptor {
        return this.description(value)
    }

    protected infix fun ParameterDescriptor.withEnum(enumClass: KClass<out Enum<*>>): ParameterDescriptor {
        val enumValues = enumClass.java.enumConstants
        val enumDescription = enumValues.joinToString(", ") { it.name }
        val currentDescription = this.description
        return this.description("$currentDescription ($enumDescription)")
    }

    protected infix fun FieldDescriptor.withEnum(enumClass: KClass<out Enum<*>>): FieldDescriptor {
        val enumValues = enumClass.java.enumConstants
        val enumDescription = enumValues.joinToString(", ") { it.name }
        val currentDescription = this.description
        return this.description("$currentDescription ($enumDescription)")
    }

    protected infix fun ParameterDescriptor.isOptional(value: Boolean): ParameterDescriptor {
        return if (value) this.optional() else this
    }

    protected infix fun FieldDescriptor.isOptional(value: Boolean): FieldDescriptor {
        return if (value) this.optional() else this
    }


    protected fun fieldsWithBasic(vararg fieldDescriptors: FieldDescriptor): List<FieldDescriptor> {
        val basicFields = mutableListOf(
            "status" type JsonFieldType.STRING means "결과 상태",
            "serverDatetime" type JsonFieldType.STRING means "응답 시간 (yyyy-MM-dd HH:mm:ss)",
            "message" type JsonFieldType.STRING means "메시지"
        )

        basicFields.addAll(fieldDescriptors)
        return basicFields
    }
}