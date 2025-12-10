package org.balanceeat.domain.diet

import com.querydsl.jpa.impl.JPAQueryFactory
import org.balanceeat.domain.apppush.QNotificationDevice.Companion.notificationDevice
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

    override fun findUserIdsWithoutDietForMealOnDate(mealType: Diet.MealType, targetDate: LocalDate): List<Long> {
        val userIdsWithDiet = jpaQueryFactory
            .select(diet.userId)
            .from(diet)
            .where(
                diet.mealType.eq(mealType)
                    .and(diet.consumedAt.dayOfMonth().eq(targetDate.dayOfMonth))
                    .and(diet.consumedAt.month().eq(targetDate.monthValue))
                    .and(diet.consumedAt.year().eq(targetDate.year))
            )
            .fetch()

        return jpaQueryFactory
            .select(notificationDevice.userId).distinct()
            .from(notificationDevice)
            .where(
                notificationDevice.isActive.isTrue
                    .and(notificationDevice.userId.notIn(userIdsWithDiet))
            )
            .fetch()
    }
}