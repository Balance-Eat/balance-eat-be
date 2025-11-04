package org.balanceeat.domain.stats

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID
import java.time.LocalDate

@Entity
@Table(name = "diet_stats")
class DietStats(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = NEW_ID,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "stats_date", nullable = false)
    val statsDate: LocalDate,

    @Column(name = "total_calories", nullable = false)
    var totalCalories: Double,

    @Column(name = "total_carbohydrates", nullable = false)
    var totalCarbohydrates: Double,

    @Column(name = "total_protein", nullable = false)
    var totalProtein: Double,

    @Column(name = "total_fat", nullable = false)
    var totalFat: Double
) : BaseEntity() {
    companion object {
        fun empty(userId: Long, statsDate: LocalDate): DietStats {
            return DietStats(
                userId = userId,
                statsDate = statsDate,
                totalCalories = 0.0,
                totalCarbohydrates = 0.0,
                totalProtein = 0.0,
                totalFat = 0.0
            )
        }
    }

    override fun guard() {
        require(userId > 0) { "사용자 ID는 0보다 큰 값이어야 합니다" }
        require(totalCalories >= 0) { "총 칼로리는 0 이상이어야 합니다" }
        require(totalCarbohydrates >= 0) { "총 탄수화물은 0 이상이어야 합니다" }
        require(totalProtein >= 0) { "총 단백질은 0 이상이어야 합니다" }
        require(totalFat >= 0) { "총 지방은 0 이상이어야 합니다" }
    }

    fun update(newDietStats: DietStats) {
        totalCalories = newDietStats.totalCalories
        totalCarbohydrates = newDietStats.totalCarbohydrates
        totalProtein = newDietStats.totalProtein
        totalFat = newDietStats.totalFat
    }
}
