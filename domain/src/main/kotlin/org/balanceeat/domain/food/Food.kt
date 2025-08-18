package org.balanceeat.domain.food

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID

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
    
    @Column(name = "serving_size", nullable = false)
    val servingSize: Double,
    
    @Column(name = "serving_unit", length = 20, nullable = false)
    val servingUnit: String,
    
    @Column(name = "calories_per_serving", nullable = false)
    val caloriesPerServing: Double,
    
    @Column(name = "carbohydrates_per_serving")
    val carbohydratesPerServing: Double? = null,
    
    @Column(name = "protein_per_serving")
    val proteinPerServing: Double? = null,
    
    @Column(name = "fat_per_serving")
    val fatPerServing: Double? = null,
    
    @Column(name = "sugar_per_serving")
    val sugarPerServing: Double? = null,
    
    @Column(name = "sodium_per_serving")
    val sodiumPerServing: Double? = null,
    
    @Column(name = "fiber_per_serving")
    val fiberPerServing: Double? = null,
    
    @Column(name = "description", length = 500)
    val description: String? = null,
    
    @Column(name = "barcode", length = 50)
    val barcode: String? = null,
    
    @Column(name = "category", length = 50)
    val category: String? = null,
    
    @Column(name = "is_verified", nullable = false)
    val isVerified: Boolean = false
) : BaseEntity() {

    fun calculateNutrition(actualServingSize: Double): NutritionInfo {
        val ratio = actualServingSize / servingSize
        return NutritionInfo(
            calories = caloriesPerServing * ratio,
            carbohydrates = carbohydratesPerServing?.let { it * ratio },
            protein = proteinPerServing?.let { it * ratio },
            fat = fatPerServing?.let { it * ratio },
            sugar = sugarPerServing?.let { it * ratio },
            sodium = sodiumPerServing?.let { it * ratio },
            fiber = fiberPerServing?.let { it * ratio }
        )
    }
    
    data class NutritionInfo(
        val calories: Double,
        val carbohydrates: Double?,
        val protein: Double?,
        val fat: Double?,
        val sugar: Double?,
        val sodium: Double?,
        val fiber: Double?
    )
}