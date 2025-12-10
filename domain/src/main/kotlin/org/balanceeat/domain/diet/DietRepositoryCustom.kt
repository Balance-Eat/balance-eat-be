package org.balanceeat.domain.diet

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

interface DietRepositoryCustom {
    fun findDailyDiets(userId: Long, date: LocalDate): List<Diet>
    fun findMonthlyDiets(userId: Long, yearMonth: YearMonth): List<Diet>
    fun existsByUserIdAndDateAndMealType(userId: Long, date: LocalDate, mealType: Diet.MealType): Boolean
    fun findUserIdsWithoutDietForMealOnDate(mealType: Diet.MealType, targetDate: LocalDate): List<Long>
}