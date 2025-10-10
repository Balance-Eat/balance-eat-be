package org.balanceeat.domain.diet

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID
import org.balanceeat.domain.food.Food
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
    var mealType: MealType,

    @Column(name = "consumed_at", nullable = false)
    var consumedAt: LocalDateTime,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id", nullable = false)
    val dietFoods: MutableList<DietFood> = mutableListOf()
) : BaseEntity() {
    companion object {
        const val MAX_FOOD_SIZE = 10
    }

    override fun guard() {
        require(userId > 0) { "사용자 ID는 0보다 큰 값이어야 합니다" }
        require(dietFoods.isNotEmpty()) { "식단에는 최소 1개 이상의 음식이 포함되어야 합니다" }
        require(dietFoods.size <= MAX_FOOD_SIZE) { "식단에는 최대 50개까지 음식을 포함할 수 있습니다" }
    }
    
    fun addFood(food: Food, intake: Int) {
        val newDietFood = DietFood(food, intake)
        dietFoods.add(newDietFood)
    }

    fun update(mealType: MealType, consumedAt: LocalDateTime) {
        this.mealType = mealType
        this.consumedAt = consumedAt
    }

    fun updateFoods(dietFoods: List<DietFood>) {
        this.dietFoods.clear()
        this.dietFoods.addAll(dietFoods)
    }

    fun removeFood(dietFoodId: Long) {
        val dietFood = dietFoods.find { it.id == dietFoodId }
            ?: throw IllegalArgumentException("식단 음식을 찾을 수 없습니다")
        dietFoods.remove(dietFood)
    }

    enum class MealType {
        BREAKFAST,
        LUNCH,
        DINNER,
        SNACK
    }
}