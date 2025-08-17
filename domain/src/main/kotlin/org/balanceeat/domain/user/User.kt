package org.balanceeat.domain.user

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID
import java.math.BigDecimal

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
    @Column(precision = 5, scale = 2, nullable = false)
    var weight: BigDecimal,
    @Column(precision = 5, scale = 2, nullable = false)
    var height: BigDecimal,
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", length = 20)
    var activityLevel: ActivityLevel? = null,
    @Column(precision = 5, scale = 2)
    var smi: BigDecimal? = null,
    @Column(name = "fat_percentage", precision = 5, scale = 2)
    var fatPercentage: BigDecimal? = null,
    @Column(name = "target_weight", precision = 5, scale = 2)
    var targetWeight: BigDecimal? = null,
    @Column(name = "target_calorie")
    var targetCalorie: Int? = null,
    @Column(name = "target_smi", precision = 5, scale = 2)
    var targetSmi: BigDecimal? = null,
    @Column(name = "target_fat_percentage", precision = 5, scale = 2)
    var targetFatPercentage: BigDecimal? = null,
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

    enum class ActivityLevel(val factor: BigDecimal) {
        // 거의 활동 안 함
        SEDENTARY(BigDecimal("1.2")),
        // 가벼운 활동 (주 1~3회)
        LIGHT(BigDecimal("1.375")),
        // 보통 활동 (주 3~5회)
        MODERATE(BigDecimal("1.55")),
        // 활발한 활동 (매일 격렬)
        ACTIVE(BigDecimal("1.725")),
        // 매우 활발한 활동 (전문 선수 수준)
        VERY_ACTIVE(BigDecimal("1.9")),
    }
}
