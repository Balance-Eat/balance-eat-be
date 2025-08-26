package org.balanceeat.api.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.Charset

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
class RequestResponseLoggingFilter(
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    private val logger = KotlinLogging.logger {}

    companion object {
        private const val REQ_ID_HEADER = "X-Request-Id"
        private const val MDC_REQUEST_ID = "requestId"
        private const val MAX_PAYLOAD_LEN = 10_000 // 10 KB

        private val TEXTUAL_MEDIA_TYPES = listOf(
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.TEXT_PLAIN,
            MediaType.TEXT_XML,
            MediaType.APPLICATION_FORM_URLENCODED
        )
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val startTime = System.currentTimeMillis()

        if (!request.requestURI.contains("/v1")) {
            filterChain.doFilter(request, response)
            return
        }

        // Wrap to enable body caching
        val wrappedRequest = request as? ContentCachingRequestWrapper ?: ContentCachingRequestWrapper(request)
        val wrappedResponse = response as? ContentCachingResponseWrapper ?: ContentCachingResponseWrapper(response)

        val reqPayload = buildRequestPayload(wrappedRequest)
        val queryForField = queryForField(request)
        MDC.put("requestBody", reqPayload)
        MDC.put("queryParams", queryForField)

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            try {
                val durationMs = System.currentTimeMillis() - startTime
                val status = wrappedResponse.status


                val resPayload = buildResponsePayload(wrappedResponse)

                // Emit one consolidated log block without extra indentation
                val pathQuerySuffix = querySuffixForPath(request)

                val logMsg = StringBuilder()
                    .append("[").append(request.method).append("] ")
                    .append(request.requestURI).append(pathQuerySuffix)
                    .append(" status=").append(status)
                    .append(" duration=").append(durationMs).append("ms\n")
                    .append("QueryParams=").append(queryForField).append("\n")
                    .append("RequestBody=").append(reqPayload).append("\n")
                    .append("ResponseBody=").append(resPayload)
                    .toString()
                logger.info { logMsg }
                // Ensure body is written back to client
                wrappedResponse.copyBodyToResponse()
            } catch (logEx: Exception) {
                // Do not break the response on logging failures
                logger.warn(logEx) { "Failed to log request/response" }
            } finally {
                MDC.clear()
            }
        }
    }

    private fun queryStringOf(request: HttpServletRequest): String {
        val qs = request.queryString
        return if (qs.isNullOrBlank()) "" else "?$qs"
    }

    private fun querySuffixForPath(request: HttpServletRequest): String {
        val qs = request.queryString
        return if (qs.isNullOrBlank()) "" else "?$qs"
    }

    private fun queryForField(request: HttpServletRequest): String {
        val qs = request.queryString
        return if (qs.isNullOrBlank()) "[None]" else qs
    }

    private fun headersOf(request: HttpServletRequest): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val names = request.headerNames
        while (names.hasMoreElements()) {
            val name = names.nextElement()
            // Avoid logging sensitive headers; extend as needed
            if (name.equals("authorization", ignoreCase = true)) continue
            result[name] = request.getHeader(name)
        }
        // Include synthesized request id header
        val currentReqId = MDC.get(MDC_REQUEST_ID)
        if (!currentReqId.isNullOrBlank() && !result.containsKey(REQ_ID_HEADER)) {
            result[REQ_ID_HEADER] = currentReqId
        }
        return result
    }

    private fun headersOf(response: HttpServletResponse): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for (name in response.headerNames) {
            result[name] = response.getHeader(name)
        }
        // Include request id if available
        val currentReqId = MDC.get(MDC_REQUEST_ID)
        if (!currentReqId.isNullOrBlank()) {
            try { response.setHeader(REQ_ID_HEADER, currentReqId) } catch (_: Exception) {}
            result[REQ_ID_HEADER] = currentReqId
        }
        return result
    }

    private fun isTextual(contentType: String?): Boolean {
        if (contentType.isNullOrBlank()) return false
        return try {
            val mt = MediaType.parseMediaType(contentType)
            TEXTUAL_MEDIA_TYPES.any { it.includes(mt) } || mt.type == "text"
        } catch (_: Exception) {
            false
        }
    }

    private fun buildRequestPayload(request: ContentCachingRequestWrapper): String {
        return try {
            val contentType = request.contentType
            if (!isTextual(contentType)) return "[None]"
            val buf = request.contentAsByteArray
            if (buf.isEmpty()) return "[None]"
            val charset = resolveCharset(contentType, request.characterEncoding)
            val text = String(buf, charset)
            val compact = minifyIfJsonOrTrim(text, contentType)
            abbreviate(compact)
        } catch (e: Exception) {
            "<failed to read: ${e.message}>"
        }
    }

    private fun buildResponsePayload(response: ContentCachingResponseWrapper): String {
        return try {
            val contentType = response.contentType
            if (!isTextual(contentType)) return "[None]"
            val buf = response.contentAsByteArray
            if (buf.isEmpty()) return "[None]"
            val charset = resolveCharset(contentType, response.characterEncoding)
            val text = String(buf, charset)
            val compact = minifyIfJsonOrTrim(text, contentType)
            abbreviate(compact)
        } catch (e: Exception) {
            "<failed to read: ${e.message}>"
        }
    }

    private fun resolveCharset(contentType: String?, encoding: String?): Charset {
        // Prefer UTF-8 for JSON per RFC 8259 when charset is missing or incorrectly set to ISO-8859-1 by container defaults
        val enc = (encoding ?: "").trim()
        val isIso88591 = enc.equals("ISO-8859-1", ignoreCase = true)
        val mediaType = try {
            if (contentType.isNullOrBlank()) null else MediaType.parseMediaType(contentType)
        } catch (_: Exception) { null }

        val isJson = try {
            if (mediaType == null) false else (mediaType.subtype.equals("json", true) || mediaType.subtype.endsWith("+json", true))
        } catch (_: Exception) { false }

        return try {
            when {
                // For JSON, default to UTF-8 if charset is missing or set to ISO-8859-1 (common container default)
                isJson && (enc.isEmpty() || isIso88591) -> Charsets.UTF_8
                // For other textual types, if charset missing, default to UTF-8
                enc.isEmpty() -> Charsets.UTF_8
                else -> Charset.forName(enc)
            }
        } catch (_: Exception) {
            Charsets.UTF_8
        }
    }

    private fun minifyIfJsonOrTrim(text: String, contentType: String?): String {
        if (text.isBlank()) return "[None]"
        val isJson = try {
            if (contentType.isNullOrBlank()) false else {
                val mt = MediaType.parseMediaType(contentType)
                mt.subtype.equals("json", true) || mt.subtype.endsWith("+json", true)
            }
        } catch (_: Exception) { false }

        if (isJson || text.trimStart().startsWith("{") || text.trimStart().startsWith("[")) {
            return try {
                val tree = objectMapper.readTree(text)
                objectMapper.writeValueAsString(tree)
            } catch (_: Exception) {
                // Fall back to removing newlines and excessive spaces
                text.replace("\n", "").replace("\r", "").replace("\t", " ").replace(Regex("\\s+"), " ").trim()
            }
        }
        // For non-JSON textual bodies, collapse excessive whitespace
        return text.replace("\n", " ").replace("\r", " ").replace("\t", " ").replace(Regex("\\s+"), " ").trim()
    }

    private fun abbreviate(text: String): String {
        return if (text.length <= MAX_PAYLOAD_LEN) text else text.substring(0, MAX_PAYLOAD_LEN) + "...<truncated>"
    }
}
