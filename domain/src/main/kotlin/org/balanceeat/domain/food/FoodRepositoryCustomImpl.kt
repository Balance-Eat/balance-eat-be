package org.balanceeat.domain.food

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Wildcard
import com.querydsl.jpa.impl.JPAQueryFactory
import org.balanceeat.domain.common.repository.BaseQueryRepository
import org.balanceeat.domain.food.QFood.Companion.food
import org.springframework.data.domain.Page

class FoodRepositoryCustomImpl(
    queryFactory: JPAQueryFactory
): BaseQueryRepository(queryFactory), FoodRepositoryCustom {
    override fun search(query: FoodQuery.Search): Page<FoodSearchResult> {
        val where = mutableListOf<BooleanExpression>()

        query.foodName?.let { where.add(food.name.containsIgnoreCase(it)) }
        query.userId?.let { where.add(food.userId.eq(it)) }

        val baseQuery = queryFactory
            .from(food)
            .where(*where.toTypedArray())

        val totalCount = baseQuery
            .select(Wildcard.count)
            .fetchOne() ?: 0L

        if (totalCount == 0L) {
            return Page.empty()
        }

        return baseQuery
            .select(
                QFoodSearchResult(
                    food.id,
                    food.uuid,
                    food.name,
                    food.userId,
                    food.servingSize,
                    food.unit,
                    food.perServingCalories,
                    food.carbohydrates,
                    food.protein,
                    food.fat,
                    food.brand,
                    food.isAdminApproved,
                    food.createdAt,
                    food.updatedAt
                )
            )
            .orderBy(food.createdAt.desc())
            .toPage(query.pageable, totalCount)
    }
}