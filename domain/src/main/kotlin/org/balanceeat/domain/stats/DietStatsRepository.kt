package org.balanceeat.domain.stats

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface DietStatsRepository : JpaRepository<DietStats, Long>, DietStatsRepositoryCustom {
    @Modifying
    @Query("DELETE FROM DietStats ds WHERE ds.statsDate = :statsDate")
    fun deleteByStatsDate(@Param("statsDate") statsDate: LocalDate): Int
}

