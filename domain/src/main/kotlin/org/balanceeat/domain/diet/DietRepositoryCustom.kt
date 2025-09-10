package org.balanceeat.domain.diet

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface DietRepositoryCustom {
    fun findDailyDiets(userId: Long, date: LocalDate): List<Diet>
}