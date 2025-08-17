package org.balanceeat.domain.diet

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID
import java.math.BigDecimal

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
    
    @Column(name = "actual_serving_size", precision = 8, scale = 2, nullable = false)
    val actualServingSize: BigDecimal,
    
    @Column(name = "serving_unit", length = 20, nullable = false)
    val servingUnit: String,
    
    @Column(name = "calculated_calories", precision = 8, scale = 2, nullable = false)
    val calculatedCalories: BigDecimal,
    
    @Column(name = "calculated_carbohydrates", precision = 8, scale = 2)
    val calculatedCarbohydrates: BigDecimal? = null,
    
    @Column(name = "calculated_protein", precision = 8, scale = 2)
    val calculatedProtein: BigDecimal? = null,
    
    @Column(name = "calculated_fat", precision = 8, scale = 2)
    val calculatedFat: BigDecimal? = null,
    
    @Column(name = "calculated_sugar", precision = 8, scale = 2)
    val calculatedSugar: BigDecimal? = null,
    
    @Column(name = "calculated_sodium", precision = 8, scale = 2)
    val calculatedSodium: BigDecimal? = null,
    
    @Column(name = "calculated_fiber", precision = 8, scale = 2)
    val calculatedFiber: BigDecimal? = null
) : BaseEntity()