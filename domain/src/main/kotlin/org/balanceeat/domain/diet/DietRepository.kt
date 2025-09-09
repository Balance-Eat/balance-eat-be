package org.balanceeat.domain.diet

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface DietRepository : JpaRepository<Diet, Long> {
    
    @Query("SELECT d FROM Diet d WHERE d.userId = :userId AND DATE(d.consumedAt) = :date ORDER BY d.mealType, d.createdAt")
    fun findByUserIdAndMealDateOrderByMealTypeAndCreatedAt(
        @Param("userId") userId: Long, 
        @Param("date") date: LocalDate
    ): List<Diet>
}