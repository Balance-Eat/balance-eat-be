package org.balanceeat.domain.common.repository

import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.balanceeat.domain.common.repository.BaseQueryRepository.Companion.BULK_SIZE_LIMIT
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

abstract class BaseQueryRepository(
    protected val queryFactory: JPAQueryFactory
) {
    companion object {
        const val BULK_SIZE_LIMIT = 1000
    }

    fun <T> JPQLQuery<T>.toPage(
        pageable: Pageable,
        totalCount: Long
    ): Page<T> {
        val content = this
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        if (totalCount == 0L) {
            return Page.empty()
        }

        return PageableExecutionUtils.getPage<T>(content, pageable) { totalCount }
    }
}

class ExceededBulkLimitException(
    size: Int = BULK_SIZE_LIMIT
) : RuntimeException("Bulk insert $size exceeds the limit")