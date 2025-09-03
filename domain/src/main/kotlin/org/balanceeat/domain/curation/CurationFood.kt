package org.balanceeat.domain.curation

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID

@Entity
@Table(name = "curation_food")
class CurationFood(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = NEW_ID,

    @Column(name = "food_id", nullable = false)
    val foodId: Long,

    @Column(name = "weight", nullable = false)
    var weight: Int
) : BaseEntity() {
    
    override fun guard() {
        require(weight > 0) { "가중치는 0보다 큰 값이어야 합니다" }
    }
    
    fun updateWeight(weight: Int) {
        this.weight = weight
    }
}