package org.balanceeat.domain.diet

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface DietFoodRepository : JpaRepository<DietFood, Long> {
    
    fun findByDietId(dietId: Long): List<DietFood>
    
    fun findByFoodId(foodId: Long): List<DietFood>
    
    @Query("SELECT df FROM DietFood df JOIN Diet d ON df.dietId = d.id WHERE d.userId = :userId AND d.mealDate = :mealDate")
    fun findByUserIdAndMealDate(@Param("userId") userId: Long, @Param("mealDate") mealDate: LocalDate): List<DietFood>
    
    @Query("SELECT df FROM DietFood df JOIN Diet d ON df.dietId = d.id WHERE d.userId = :userId AND d.mealDate BETWEEN :startDate AND :endDate")
    fun findByUserIdAndDateRange(
        @Param("userId") userId: Long,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<DietFood>
    
    @Query("SELECT df FROM DietFood df JOIN Diet d ON df.dietId = d.id WHERE d.userId = :userId AND d.mealType = :mealType AND d.mealDate = :mealDate")
    fun findByUserIdAndMealTypeAndDate(
        @Param("userId") userId: Long,
        @Param("mealType") mealType: Diet.MealType,
        @Param("mealDate") mealDate: LocalDate
    ): List<DietFood>
}