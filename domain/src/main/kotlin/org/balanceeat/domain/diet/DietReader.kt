package org.balanceeat.domain.diet

import org.balanceeat.domain.common.BaseReader
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.food.FoodRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@Component
@Transactional(readOnly = true)
class DietReader(
    private val dietRepository: DietRepository,
    private val foodRepository: FoodRepository
): BaseReader<Diet, DietResult>(dietRepository, DietResult) {

    override fun findByIdOrThrow(id: Long): DietResult {
        return findByIdOrThrow(id, DomainStatus.DIET_NOT_FOUND)
    }

    fun findDailyDietSummaries(userId: Long, date: LocalDate): List<DietSummary> {
        val diets = dietRepository.findDailyDiets(userId, date)
        return dietsToSummaries(diets)
    }

    fun findMonthlyDietSummaries(userId: Long, yearMonth: YearMonth): List<DietSummary> {
        val diets = dietRepository.findMonthlyDiets(userId, yearMonth)
        return dietsToSummaries(diets)
    }

    fun findUserIdsWithoutDietForMealOnDate(mealType: Diet.MealType, targetDate: LocalDate): List<Long> {
        return dietRepository.findUserIdsWithoutDietForMealOnDate(mealType, targetDate)
    }

    private fun dietsToSummaries(diets: List<Diet>): List<DietSummary> {
        val foodIds = diets.flatMap { it.dietFoods.map { dietFood -> dietFood.foodId } }.toList()
        val foodMap = foodRepository.findAllById(foodIds).associateBy { it.id }
        return diets.map { DietSummary.of(it, foodMap) }
    }
}
