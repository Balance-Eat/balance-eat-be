package org.balanceeat.domain.food

import java.math.BigDecimal

sealed class FoodCommand {
    
    data class Create(
        val name: String,
        val brand: String? = null,
        val servingSize: BigDecimal,
        val servingUnit: String,
        val caloriesPerServing: BigDecimal,
        val carbohydratesPerServing: BigDecimal? = null,
        val proteinPerServing: BigDecimal? = null,
        val fatPerServing: BigDecimal? = null,
        val sugarPerServing: BigDecimal? = null,
        val sodiumPerServing: BigDecimal? = null,
        val fiberPerServing: BigDecimal? = null,
        val description: String? = null,
        val barcode: String? = null,
        val category: String? = null,
        val isVerified: Boolean = false
    )
    
    data class Update(
        val name: String? = null,
        val brand: String? = null,
        val servingSize: BigDecimal? = null,
        val servingUnit: String? = null,
        val caloriesPerServing: BigDecimal? = null,
        val carbohydratesPerServing: BigDecimal? = null,
        val proteinPerServing: BigDecimal? = null,
        val fatPerServing: BigDecimal? = null,
        val sugarPerServing: BigDecimal? = null,
        val sodiumPerServing: BigDecimal? = null,
        val fiberPerServing: BigDecimal? = null,
        val description: String? = null,
        val barcode: String? = null,
        val category: String? = null,
        val isVerified: Boolean? = null
    )
}