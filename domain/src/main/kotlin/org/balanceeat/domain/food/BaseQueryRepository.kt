package org.balanceeat.domain.food

import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

abstract class BaseQueryRepository(
    protected val queryFactory: JPAQueryFactory
) {
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