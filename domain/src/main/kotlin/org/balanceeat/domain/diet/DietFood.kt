package org.balanceeat.domain.diet

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID

@Entity
@Table(name = "diet_food")
class DietFood(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = NEW_ID,
    
    @Column(name = "diet_id", nullable = false)
    val dietId: Long,

    @Column(name = "food_id", nullable = false)
    val foodId: Long,
    
    @Column(name = "actual_serving_size", nullable = false)
    val actualServingSize: Double,
    
    @Column(name = "serving_unit", length = 20, nullable = false)
    val servingUnit: String,
    
    @Column(name = "calculated_calories", nullable = false)
    val calculatedCalories: Double,
    
    @Column(name = "calculated_carbohydrates")
    val calculatedCarbohydrates: Double? = null,
    
    @Column(name = "calculated_protein")
    val calculatedProtein: Double? = null,
    
    @Column(name = "calculated_fat")
    val calculatedFat: Double? = null,
    
    @Column(name = "calculated_sugar")
    val calculatedSugar: Double? = null,
    
    @Column(name = "calculated_sodium")
    val calculatedSodium: Double? = null,
    
    @Column(name = "calculated_fiber")
    val calculatedFiber: Double? = null
) : BaseEntity()