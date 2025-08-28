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

    @Column(name = "uuid", nullable = false, unique = true, length = 36)
    val uuid: String,

    // 1회 기준 섭취량
    @Column(name = "per_capita_intake", nullable = false)
    val perCapitaIntake: Double,

    // 단위 (예: mg, ml, g 등)
    @Column(name = "unit", length = 20, nullable = false)
    val unit: String,

    @Column(name = "carbohydrates", nullable = false)
    val carbohydrates: Double = 0.0,

    @Column(name = "protein", nullable = false)
    val protein: Double = 0.0,

    @Column(name = "fat", nullable = false)
    val fat: Double = 0.0,

    // 관리자 검수 여부
    @Column(name = "is_verified", nullable = false)
    val isVerified: Boolean = false
) : BaseEntity() {
    override fun guard() {
        require(name.isNotBlank()) { "음식명은 필수값입니다" }
        require(name.length <= 100) { "음식명은 100자를 초과할 수 없습니다" }
        
        require(uuid.isNotBlank()) { "UUID는 필수값입니다" }
        require(uuid.length <= 36) { "UUID는 36자를 초과할 수 없습니다" }

        require(perCapitaIntake > 0.0) { "1회 기준 섭취량은 0보다 큰 값이어야 합니다" }
        require(perCapitaIntake <= 10000.0) { "1회 기준 섭취량은 10000을 초과할 수 없습니다" }

        require(unit.isNotBlank()) { "단위는 필수값입니다" }
        require(unit.length <= 20) { "단위는 20자를 초과할 수 없습니다" }

        require(carbohydrates >= 0.0) { "탄수화물 함량은 0 이상이어야 합니다" }
        require(carbohydrates <= 1000.0) { "탄수화물 함량은 1000g을 초과할 수 없습니다" }

        require(protein >= 0.0) { "단백질 함량은 0 이상이어야 합니다" }
        require(protein <= 1000.0) { "단백질 함량은 1000g을 초과할 수 없습니다" }

        require(fat >= 0.0) { "지방 함량은 0 이상이어야 합니다" }
        require(fat <= 1000.0) { "지방 함량은 1000g을 초과할 수 없습니다" }
    }

    fun calculateNutrition(actualServingSize: Double): NutritionInfo {
        val ratio = actualServingSize / perCapitaIntake
        
        return NutritionInfo(
            calories = calculateCalories(ratio),
            carbohydrates = carbohydrates * ratio,
            protein = protein * ratio,
            fat = fat * ratio
        )
    }
    
    private fun calculateCalories(ratio: Double): Double {
        // 칼로리 계산: 탄수화물(4kcal/g) + 단백질(4kcal/g) + 지방(9kcal/g)
        return (carbohydrates * 4 + protein * 4 + fat * 9) * ratio
    }
    
    data class NutritionInfo(
        val calories: Double,
        val carbohydrates: Double,
        val protein: Double,
        val fat: Double
    )
}