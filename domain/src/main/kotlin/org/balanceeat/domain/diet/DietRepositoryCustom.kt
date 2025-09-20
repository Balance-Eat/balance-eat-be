package org.balanceeat.domain.diet

import java.time.LocalDate

interface DietRepositoryCustom {
    fun findDailyDiets(userId: Long, date: LocalDate): List<Diet>
}