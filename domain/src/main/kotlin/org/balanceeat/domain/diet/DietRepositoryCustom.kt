package org.balanceeat.domain.diet

import java.time.LocalDate
import java.time.YearMonth

interface DietRepositoryCustom {
    fun findDailyDiets(userId: Long, date: LocalDate): List<Diet>
    fun findMonthlyDiets(userId: Long, yearMonth: YearMonth): List<Diet>
}