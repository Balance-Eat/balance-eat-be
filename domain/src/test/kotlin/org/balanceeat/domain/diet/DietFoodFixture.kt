package org.balanceeat.domain.diet

import org.balanceeat.domain.config.TestFixture
import org.balanceeat.domain.config.NEW_ID

class DietFoodFixture(
    var id: Long = NEW_ID,
    var dietId: Long = 1L,
    var foodId: Long = 1L,
    var actualServingSize: Double = 1.0,
    var servingUnit: String = "개",
    var calculatedCalories: Double = 100.0,
    var calculatedCarbohydrates: Double = 20.0,
    var calculatedProtein: Double = 5.0,
    var calculatedFat: Double = 2.0,
    var calculatedSugar: Double = 1.0,
    var calculatedSodium: Double = 100.0,
    var calculatedFiber: Double = 1.0
) : TestFixture<DietFood> {
    override fun create(): DietFood {
        return DietFood(
            id = id,
            dietId = dietId,
            foodId = foodId,
            actualServingSize = actualServingSize,
            servingUnit = servingUnit,
            calculatedCalories = calculatedCalories,
            calculatedCarbohydrates = calculatedCarbohydrates,
            calculatedProtein = calculatedProtein,
            calculatedFat = calculatedFat,
            calculatedSugar = calculatedSugar,
            calculatedSodium = calculatedSodium,
            calculatedFiber = calculatedFiber
        )
    }
}