package org.balanceeat.apibase.response

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.Page
import kotlin.math.ceil

data class PageResponse<T>(
    val totalItems: Long,
    val currentPage: Int,
    val itemsPerPage: Int,
    val items: List<T>,
) {
    companion object {
        fun <T> from(page: Page<T>): PageResponse<T> = PageResponse(
                page.totalElements,
                page.number + 1,
                page.size,
                page.getContent(),
            )
    }

    @JsonProperty
    fun totalPages(): Int = if (this.totalItems < this.itemsPerPage.toLong()) {
            1
        } else {
            ceil(this.totalItems.toDouble() / this.itemsPerPage.toDouble())
                .toInt()
        }
}
