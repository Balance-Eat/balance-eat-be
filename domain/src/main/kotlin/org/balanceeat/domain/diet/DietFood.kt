package org.balanceeat.domain.diet

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID
import org.balanceeat.domain.food.Food

@Entity
@Table(name = "diet_food")
class DietFood(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = NEW_ID,

    @Column(name = "food_id", nullable = false)
    val foodId: Long,

    // 섭취량 ex) 3 -> 3개
    @Column(name = "intake", nullable = false)
    var intake: Int
) : BaseEntity() {
    constructor(food: Food, intake: Int) : this(
        foodId = food.id,
        intake = intake
    )

    override fun guard() {
        require(foodId > 0) { "음식 ID는 0보다 큰 값이어야 합니다" }
        require(intake > 0) { "섭취량은 0보다 큰 값이어야 합니다" }
        require(intake <= 100) { "섭취량은 100개를 초과할 수 없습니다" }
    }
}