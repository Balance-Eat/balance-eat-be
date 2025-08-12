package org.balanceeat.domain.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDomainService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(command: UserCommand.Create) {
        val user = User(
            name = command.name,
            uuid = command.uuid,
            email = command.email,
            gender = command.gender,
            age = command.age,
            weight = command.weight,
            height = command.height,
            activityLevel = command.activityLevel,
            smi = command.smi,
            fatPercentage = command.fatPercentage,
            targetWeight = command.targetWeight,
            targetCalorie = command.targetCalorie,
            targetSmi = command.targetSmi,
            targetFatPercentage = command.targetFatPercentage,
            providerId = command.providerId,
            providerType = command.providerType
        )
        userRepository.save(user)
    }
}
