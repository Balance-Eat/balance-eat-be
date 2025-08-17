package org.balanceeat.domain.user

import org.balanceeat.domain.config.FixtureReflectionUtils
import org.balanceeat.domain.config.TestFixture
import java.math.BigDecimal
import java.util.UUID

class UserFixture : TestFixture<User> {
    private var id: Long = 1L
    private var name: String = "테스트유저"
    private var uuid: String = UUID.randomUUID().toString()
    private var email: String? = "test@example.com"
    private var gender: User.Gender = User.Gender.MALE
    private var age: Int = 25
    private var weight: BigDecimal = BigDecimal("70.0")
    private var height: BigDecimal = BigDecimal("170.0")
    private var activityLevel: User.ActivityLevel? = User.ActivityLevel.MODERATE
    private var smi: BigDecimal? = null
    private var fatPercentage: BigDecimal? = null
    private var targetWeight: BigDecimal? = null
    private var targetCalorie: Int? = null
    private var targetSmi: BigDecimal? = null
    private var targetFatPercentage: BigDecimal? = null
    private var providerId: String? = null
    private var providerType: String? = null

    // Builder pattern methods with method chaining
    fun setId(id: Long): UserFixture = apply { this.id = id }
    fun setName(name: String): UserFixture = apply { this.name = name }
    fun setUuid(uuid: String): UserFixture = apply { this.uuid = uuid }
    fun setEmail(email: String?): UserFixture = apply { this.email = email }
    fun setGender(gender: User.Gender): UserFixture = apply { this.gender = gender }
    fun setAge(age: Int): UserFixture = apply { this.age = age }
    fun setWeight(weight: BigDecimal): UserFixture = apply { this.weight = weight }
    fun setHeight(height: BigDecimal): UserFixture = apply { this.height = height }
    fun setActivityLevel(activityLevel: User.ActivityLevel?): UserFixture = apply { this.activityLevel = activityLevel }
    fun setSmi(smi: BigDecimal?): UserFixture = apply { this.smi = smi }
    fun setFatPercentage(fatPercentage: BigDecimal?): UserFixture = apply { this.fatPercentage = fatPercentage }
    fun setTargetWeight(targetWeight: BigDecimal?): UserFixture = apply { this.targetWeight = targetWeight }
    fun setTargetCalorie(targetCalorie: Int?): UserFixture = apply { this.targetCalorie = targetCalorie }
    fun setTargetSmi(targetSmi: BigDecimal?): UserFixture = apply { this.targetSmi = targetSmi }
    fun setTargetFatPercentage(targetFatPercentage: BigDecimal?): UserFixture = apply { this.targetFatPercentage = targetFatPercentage }
    fun setProviderId(providerId: String?): UserFixture = apply { this.providerId = providerId }
    fun setProviderType(providerType: String?): UserFixture = apply { this.providerType = providerType }

    override fun create(): User {
        val entity = User()
        return FixtureReflectionUtils.reflect(entity, this)
    }

    companion object {
        fun createUser(
            name: String = "테스트유저",
            uuid: String = UUID.randomUUID().toString(),
            email: String = "test@example.com",
            gender: User.Gender = User.Gender.MALE,
            age: Int = 25,
            weight: BigDecimal = BigDecimal("70.0"),
            height: BigDecimal = BigDecimal("170.0")
        ): User {
            return UserFixture()
                .setName(name)
                .setUuid(uuid)
                .setEmail(email)
                .setGender(gender)
                .setAge(age)
                .setWeight(weight)
                .setHeight(height)
                .setActivityLevel(User.ActivityLevel.MODERATE)
                .create()
        }
    }
}