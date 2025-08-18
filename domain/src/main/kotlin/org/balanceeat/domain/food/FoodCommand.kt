package org.balanceeat.domain.food


sealed class FoodCommand {
    
    data class Create(
        val name: String,
        val brand: String? = null,
        val servingSize: Double,
        val servingUnit: String,
        val caloriesPerServing: Double,
        val carbohydratesPerServing: Double? = null,
        val proteinPerServing: Double? = null,
        val fatPerServing: Double? = null,
        val sugarPerServing: Double? = null,
        val sodiumPerServing: Double? = null,
        val fiberPerServing: Double? = null,
        val description: String? = null,
        val barcode: String? = null,
        val category: String? = null,
        val isVerified: Boolean = false
    )
    
    data class Update(
        val name: String? = null,
        val brand: String? = null,
        val servingSize: Double? = null,
        val servingUnit: String? = null,
        val caloriesPerServing: Double? = null,
        val carbohydratesPerServing: Double? = null,
        val proteinPerServing: Double? = null,
        val fatPerServing: Double? = null,
        val sugarPerServing: Double? = null,
        val sodiumPerServing: Double? = null,
        val fiberPerServing: Double? = null,
        val description: String? = null,
        val barcode: String? = null,
        val category: String? = null,
        val isVerified: Boolean? = null
    )
}