package org.balanceeat.domain.diet

import com.querydsl.jpa.impl.JPAQueryFactory
import org.balanceeat.domain.diet.QDiet.Companion.diet
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
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
}