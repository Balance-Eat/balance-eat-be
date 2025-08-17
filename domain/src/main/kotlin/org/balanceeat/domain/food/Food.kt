package org.balanceeat.domain.food

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID
import java.math.BigDecimal

@Entity
@Table(name = "food")
class Food(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = NEW_ID,
    
    @Column(name = "name", nullable = false, length = 100)
    val name: String,
    
    @Column(name = "brand", length = 100)
    val brand: String? = null,
    
    @Column(name = "serving_size", precision = 8, scale = 2, nullable = false)
    val servingSize: BigDecimal,
    
    @Column(name = "serving_unit", length = 20, nullable = false)
    val servingUnit: String,
    
    @Column(name = "calories_per_serving", precision = 8, scale = 2, nullable = false)
    val caloriesPerServing: BigDecimal,
    
    @Column(name = "carbohydrates_per_serving", precision = 8, scale = 2)
    val carbohydratesPerServing: BigDecimal? = null,
    
    @Column(name = "protein_per_serving", precision = 8, scale = 2)
    val proteinPerServing: BigDecimal? = null,
    
    @Column(name = "fat_per_serving", precision = 8, scale = 2)
    val fatPerServing: BigDecimal? = null,
    
    @Column(name = "sugar_per_serving", precision = 8, scale = 2)
    val sugarPerServing: BigDecimal? = null,
    
    @Column(name = "sodium_per_serving", precision = 8, scale = 2)
    val sodiumPerServing: BigDecimal? = null,
    
    @Column(name = "fiber_per_serving", precision = 8, scale = 2)
    val fiberPerServing: BigDecimal? = null,
    
    @Column(name = "description", length = 500)
    val description: String? = null,
    
    @Column(name = "barcode", length = 50)
    val barcode: String? = null,
    
    @Column(name = "category", length = 50)
    val category: String? = null,
    
    @Column(name = "is_verified", nullable = false)
    val isVerified: Boolean = false
) : BaseEntity() {

    fun calculateNutrition(actualServingSize: BigDecimal): NutritionInfo {
        val ratio = actualServingSize.divide(servingSize)
        return NutritionInfo(
            calories = caloriesPerServing.multiply(ratio),
            carbohydrates = carbohydratesPerServing?.multiply(ratio),
            protein = proteinPerServing?.multiply(ratio),
            fat = fatPerServing?.multiply(ratio),
            sugar = sugarPerServing?.multiply(ratio),
            sodium = sodiumPerServing?.multiply(ratio),
            fiber = fiberPerServing?.multiply(ratio)
        )
    }
    
    data class NutritionInfo(
        val calories: BigDecimal,
        val carbohydrates: BigDecimal?,
        val protein: BigDecimal?,
        val fat: BigDecimal?,
        val sugar: BigDecimal?,
        val sodium: BigDecimal?,
        val fiber: BigDecimal?
    )
}