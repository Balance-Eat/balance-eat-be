package org.balanceeat.domain.diet

import java.time.LocalDate

class DietQuery {
    data class FindDailyDiets(
        val userId: Long,
        val date: LocalDate
    )
}