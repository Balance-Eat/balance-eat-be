package org.balanceeat.domain.diet

import com.querydsl.jpa.impl.JPAQueryFactory
import org.balanceeat.domain.diet.QDiet.Companion.diet
import java.time.LocalDate
import java.time.YearMonth

class DietRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : DietRepositoryCustom {

    override fun findDailyDiets(userId: Long, date: LocalDate): List<Diet> {
        return jpaQueryFactory
            .selectFrom(diet)
            .leftJoin(diet.dietFoods).fetchJoin()
            .where(
                diet.userId.eq(userId)
                    .and(diet.consumedAt.dayOfMonth().eq(date.dayOfMonth))
                    .and(diet.consumedAt.month().eq(date.monthValue))
                    .and(diet.consumedAt.year().eq(date.year))
            )
            .orderBy(diet.consumedAt.asc())
            .fetch()
    }

    override fun findMonthlyDiets(userId: Long, yearMonth: YearMonth): List<Diet> {
        return jpaQueryFactory
            .selectFrom(diet)
            .leftJoin(diet.dietFoods).fetchJoin()
            .where(
                diet.userId.eq(userId)
                    .and(diet.consumedAt.month().eq(yearMonth.monthValue))
                    .and(diet.consumedAt.year().eq(yearMonth.year))
            )
            .orderBy(diet.consumedAt.asc())
            .fetch()
    }

    override fun existsByUserIdAndDateAndMealType(userId: Long, date: LocalDate, mealType: Diet.MealType): Boolean {
        return jpaQueryFactory
            .selectOne()
            .from(diet)
            .where(
                diet.userId.eq(userId)
                    .and(diet.consumedAt.dayOfMonth().eq(date.dayOfMonth))
                    .and(diet.consumedAt.month().eq(date.monthValue))
                    .and(diet.consumedAt.year().eq(date.year))
                    .and(diet.mealType.eq(mealType))
            )
            .fetchFirst() != null
    }
}