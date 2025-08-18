package org.balanceeat.domain.user

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID

@Entity
@Table(name = "\"user\"")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = NEW_ID,
    @Column(nullable = true, length = 100)
    var name: String,
    @Column(nullable = false, unique = true, length = 36)
    val uuid: String,
    @Column(nullable = true, unique = false, length = 255)
    var email: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    var gender: Gender,
    @Column(nullable = false)
    var age: Int,
    @Column(nullable = false)
    var weight: Double,
    @Column(nullable = false)
    var height: Double,
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", length = 20)
    var activityLevel: ActivityLevel? = null,
    @Column
    var smi: Double? = null,
    @Column(name = "fat_percentage")
    var fatPercentage: Double? = null,
    @Column(name = "target_weight")
    var targetWeight: Double? = null,
    @Column(name = "target_calorie")
    var targetCalorie: Int? = null,
    @Column(name = "target_smi")
    var targetSmi: Double? = null,
    @Column(name = "target_fat_percentage")
    var targetFatPercentage: Double? = null,
    @Column(name = "provider_id", length = 255)
    var providerId: String? = null,
    @Column(name = "provider_type", length = 50)
    var providerType: String? = null,
) : BaseEntity() {

    enum class Gender {
        MALE,
        FEMALE,
        OTHER,
    }

    enum class ActivityLevel(val factor: Double) {
        // 거의 활동 안 함
        SEDENTARY(1.2),
        // 가벼운 활동 (주 1~3회)
        LIGHT(1.375),
        // 보통 활동 (주 3~5회)
        MODERATE(1.55),
        // 활발한 활동 (매일 격렬)
        ACTIVE(1.725),
        // 매우 활발한 활동 (전문 선수 수준)
        VERY_ACTIVE(1.9),
    }
}
