package org.balanceeat.domain.food


sealed class FoodCommand {
    
    data class Create(
        val uuid: String,
        val name: String,
        val perCapitaIntake: Double,
        val unit: String,
        val carbohydrates: Double = 0.0,
        val protein: Double = 0.0,
        val fat: Double = 0.0,
        val isVerified: Boolean = false
    )
    
    data class Update(
        val name: String? = null,
        val perCapitaIntake: Double? = null,
        val unit: String? = null,
        val carbohydrates: Double? = null,
        val protein: Double? = null,
        val fat: Double? = null,
        val isVerified: Boolean? = null
    )
}