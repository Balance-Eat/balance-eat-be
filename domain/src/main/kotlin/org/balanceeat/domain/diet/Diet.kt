package org.balanceeat.domain.diet

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "diet")
class Diet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = NEW_ID,
    
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    
    @Column(name = "meal_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val mealType: MealType,
    
    @Column(name = "meal_date", nullable = false)
    val mealDate: LocalDate,
    
    @Column(name = "consumed_at")
    var consumedAt: LocalDateTime? = null
) : BaseEntity() {
    
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id")
    val dietFoods: MutableList<DietFood> = mutableListOf()
    
    fun addFood(foodId: Long, actualServingSize: Double, servingUnit: String, foodDomainService: org.balanceeat.domain.food.FoodDomainService): DietFood {
        val food = foodDomainService.getFood(foodId)
        val nutritionInfo = food.calculateNutrition(actualServingSize)
        
        val dietFood = DietFood(
            dietId = this.id,
            foodId = foodId,
            actualServingSize = actualServingSize,
            servingUnit = servingUnit,
            calculatedCalories = nutritionInfo.calories,
            calculatedCarbohydrates = nutritionInfo.carbohydrates,
            calculatedProtein = nutritionInfo.protein,
            calculatedFat = nutritionInfo.fat,
            calculatedSugar = nutritionInfo.sugar,
            calculatedSodium = nutritionInfo.sodium,
            calculatedFiber = nutritionInfo.fiber
        )
        
        dietFoods.add(dietFood)
        return dietFood
    }
    
    fun removeFood(dietFood: DietFood) {
        dietFoods.remove(dietFood)
    }
    
    fun getTotalCalories(): Double {
        return dietFoods.sumOf { it.calculatedCalories }
    }
    
    fun getTotalCarbohydrates(): Double {
        return dietFoods.mapNotNull { it.calculatedCarbohydrates }
            .takeIf { it.isNotEmpty() }
            ?.reduce { acc, value -> acc + value }
            ?: 0.0
    }
    
    fun getTotalProtein(): Double {
        return dietFoods.mapNotNull { it.calculatedProtein }
            .takeIf { it.isNotEmpty() }
            ?.reduce { acc, value -> acc + value }
            ?: 0.0
    }
    
    fun getTotalFat(): Double {
        return dietFoods.mapNotNull { it.calculatedFat }
            .takeIf { it.isNotEmpty() }
            ?.reduce { acc, value -> acc + value }
            ?: 0.0
    }
    
    fun getTotalSugar(): Double {
        return dietFoods.mapNotNull { it.calculatedSugar }
            .takeIf { it.isNotEmpty() }
            ?.reduce { acc, value -> acc + value }
            ?: 0.0
    }
    
    fun getTotalSodium(): Double {
        return dietFoods.mapNotNull { it.calculatedSodium }
            .takeIf { it.isNotEmpty() }
            ?.reduce { acc, value -> acc + value }
            ?: 0.0
    }
    
    fun getTotalFiber(): Double {
        return dietFoods.mapNotNull { it.calculatedFiber }
            .takeIf { it.isNotEmpty() }
            ?.reduce { acc, value -> acc + value }
            ?: 0.0
    }
    
    enum class MealType(val displayName: String) {
        BREAKFAST("아침"),
        LUNCH("점심"),
        DINNER("저녁"),
        SNACK("간식")
    }
}