package org.balanceeat.domain.diet

import org.balanceeat.common.TestFixture
import org.balanceeat.domain.food.NutritionInfo
import java.time.LocalDateTime

class NutritionInfoFixture(
    var calories: Double = 159.0,
    var carbohydrates: Double = 25.0,
    var protein: Double = 8.0,
    var fat: Double = 3.0
) : TestFixture<NutritionInfo> {
    override fun create(): NutritionInfo {
        return NutritionInfo(
            calories = calories,
            carbohydrates = carbohydrates,
            protein = protein,
            fat = fat
        )
    }
}

fun nutritionInfoFixture(block: NutritionInfoFixture.() -> Unit = {}): NutritionInfo {
    return NutritionInfoFixture().apply(block).create()
}

class DietFoodResultFixture(
    val id: Long = 1L,
    var dietFoodId: Long = 1L,
    var foodId: Long = 1L,
    var intake: Int = 100,
) : TestFixture<DietFoodResult> {
    override fun create(): DietFoodResult {
        return DietFoodResult(
            id = id,
            dietFoodId = dietFoodId,
            foodId = foodId,
            intake = intake,
        )
    }
}

fun dietFoodResultFixture(block: DietFoodResultFixture.() -> Unit = {}): DietFoodResult {
    return DietFoodResultFixture().apply(block).create()
}

class DietResultFixture(
    var id: Long = 1L,
    var userId: Long = 1L,
    var mealType: Diet.MealType = Diet.MealType.BREAKFAST,
    var consumedAt: LocalDateTime = LocalDateTime.now(),
    var dietFoods: List<DietFoodResult> = listOf(dietFoodResultFixture())
) : TestFixture<DietResult> {
    override fun create(): DietResult {
        return DietResult(
            id = id,
            userId = userId,
            mealType = mealType,
            consumedAt = consumedAt,
            dietFoods = dietFoods
        )
    }
}

fun dietResultFixture(block: DietResultFixture.() -> Unit = {}): DietResult {
    return DietResultFixture().apply(block).create()
}
