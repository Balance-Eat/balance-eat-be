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
    @Column(name = "target_carbohydrates")
    var targetCarbohydrates: Double? = null,
    @Column(name = "target_protein")
    var targetProtein: Double? = null,
    @Column(name = "target_fat")
    var targetFat: Double? = null,
    @Column(name = "provider_id", length = 255)
    var providerId: String? = null,
    @Column(name = "provider_type", length = 50)
    var providerType: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", length = 20, nullable = false)
    var goalType: GoalType,
) : BaseEntity() {

    override fun guard() {
        // 기본 필드 검증
        require(name.length <= 100) { "이름은 100자를 초과할 수 없습니다" }
        require(uuid.isNotBlank()) { "UUID는 필수값입니다" }
        require(uuid.length <= 36) { "UUID는 36자를 초과할 수 없습니다" }
        
        // 이메일 검증
        email?.let {
            require(it.length <= 255) { "이메일은 255자를 초과할 수 없습니다" }
            require(it.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
                "유효한 이메일 형식이 아닙니다"
            }
        }
        
        // 나이 검증
        require(age in 1..150) { "나이는 1세 이상 150세 이하여야 합니다" }
        
        // 몸무게 검증
        require(weight in 10.0..1000.0) { "몸무게는 10kg 이상 1000kg 이하여야 합니다" }
        require(weight.toString().substringAfter(".").length <= 2) { "몸무게는 소수점 2자리까지만 입력 가능합니다" }
        
        // 키 검증 (10-300cm)
        require(height in 10.0..300.0) { "키는 10cm 이상 300cm 이하여야 합니다" }
        require(height.toString().substringAfter(".").length <= 2) { "키는 소수점 2자리까지만 입력 가능합니다" }
        
        // SMI 검증
        smi?.let {
            require(it in 0.0..50.0) { "SMI는 0 이상 50 이하여야 합니다" }
            require(it.toString().substringAfter(".").length <= 2) { "SMI는 소수점 2자리까지만 입력 가능합니다" }
        }
        
        // 체지방률 검증
        fatPercentage?.let {
            require(it in 0.0..100.0) { "체지방률은 0% 이상 100% 이하여야 합니다" }
            require(it.toString().substringAfter(".").length <= 2) { "체지방률은 소수점 2자리까지만 입력 가능합니다" }
        }
        
        // 목표 몸무게 검증
        targetWeight?.let {
            require(it in 10.0..1000.0) { "목표 몸무게는 10kg 이상 1000kg 이하여야 합니다" }
            require(it.toString().substringAfter(".").length <= 2) { "목표 몸무게는 소수점 2자리까지만 입력 가능합니다" }
        }
        
        // 목표 칼로리 검증
        targetCalorie?.let {
            require(it in 500..10000) { "목표 칼로리는 500kcal 이상 10000kcal 이하여야 합니다" }
        }
        
        // 목표 SMI 검증
        targetSmi?.let {
            require(it in 0.0..50.0) { "목표 SMI는 0 이상 50 이하여야 합니다" }
            require(it.toString().substringAfter(".").length <= 2) { "목표 SMI는 소수점 2자리까지만 입력 가능합니다" }
        }
        
        // 목표 체지방률 검증
        targetFatPercentage?.let {
            require(it in 0.0..100.0) { "목표 체지방률은 0% 이상 100% 이하여야 합니다" }
            require(it.toString().substringAfter(".").length <= 2) { "목표 체지방률은 소수점 2자리까지만 입력 가능합니다" }
        }

        // 목표 탄수화물 검증
        targetCarbohydrates?.let {
            require(it in 0.0..1000.0) { "목표 탄수화물은 0g 이상 1000g 이하여야 합니다" }
            require(it.toString().substringAfter(".").length <= 2) { "목표 탄수화물은 소수점 2자리까지만 입력 가능합니다" }
        }

        // 목표 단백질 검증
        targetProtein?.let {
            require(it in 0.0..1000.0) { "목표 단백질은 0g 이상 1000g 이하여야 합니다" }
            require(it.toString().substringAfter(".").length <= 2) { "목표 단백질은 소수점 2자리까지만 입력 가능합니다" }
        }

        // 목표 지방 검증
        targetFat?.let {
            require(it in 0.0..1000.0) { "목표 지방은 0g 이상 1000g 이하여야 합니다" }
            require(it.toString().substringAfter(".").length <= 2) { "목표 지방은 소수점 2자리까지만 입력 가능합니다" }
        }
        
        // Provider 필드 검증
        providerId?.let {
            require(it.length <= 255) { "Provider ID는 255자를 초과할 수 없습니다" }
        }
        
        providerType?.let {
            require(it.length <= 50) { "Provider Type은 50자를 초과할 수 없습니다" }
        }
    }

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
        ACTIVE(1.725)
    }

    enum class GoalType {
        // 다이어트
        DIET,
        // 벌크업
        BULK_UP,
        // 체중 유지
        MAINTAIN
    }
}