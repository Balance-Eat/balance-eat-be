package org.balanceeat.domain.common.utils

object CalorieCalculator {
    fun calculate(
        carbohydrates: Double,
        protein: Double,
        fat: Double,
    ): Double = (carbohydrates * 4) + (protein * 4) + (fat * 9)
}
