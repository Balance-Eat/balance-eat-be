package org.balanceeat.domain.food

data class NutritionInfo(
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double
) {
    companion object {
        fun of(food: Food, intake: Double): NutritionInfo {
            val factor = intake / food.servingSize
            return NutritionInfo(
                calories = food.perServingCalories * factor,
                carbohydrates = food.carbohydrates * factor,
                protein = food.protein * factor,
                fat = food.fat * factor
            )
        }
    }
}