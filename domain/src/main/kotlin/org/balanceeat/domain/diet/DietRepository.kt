package org.balanceeat.domain.diet

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface DietRepository : JpaRepository<Diet, Long> {
    
    fun findByUserIdAndMealDate(userId: Long, mealDate: LocalDate): List<Diet>
    
    fun findByUserIdAndMealDateBetween(userId: Long, startDate: LocalDate, endDate: LocalDate): List<Diet>
    
    @Query("SELECT d FROM Diet d WHERE d.userId = :userId AND d.mealDate = :mealDate ORDER BY d.mealType, d.createdAt")
    fun findByUserIdAndMealDateOrderByMealTypeAndCreatedAt(
        @Param("userId") userId: Long, 
        @Param("mealDate") mealDate: LocalDate
    ): List<Diet>
}