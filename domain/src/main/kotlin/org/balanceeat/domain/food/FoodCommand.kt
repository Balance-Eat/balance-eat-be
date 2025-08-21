package org.balanceeat.domain.food


sealed class FoodCommand {
    
    data class Create(
        val name: String,
        val perCapitaIntake: Double,
        val unit: String,
        val carbohydrates: Double = 0.0,
        val protein: Double = 0.0,
        val fat: Double = 0.0,
        val uuid: String? = null // null이면 자동 생성
    )
    
    data class Update(
        val name: String? = null,
        val perCapitaIntake: Double? = null,
        val unit: String? = null,
        val carbohydrates: Double? = null,
        val protein: Double? = null,
        val fat: Double? = null
    )
}