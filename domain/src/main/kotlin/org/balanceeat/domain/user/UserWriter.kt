package org.balanceeat.domain.user

import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.DomainStatus.USER_ALREADY_EXISTS
import org.balanceeat.domain.common.exception.BadCommandException
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserWriter(
    private val userRepository: UserRepository
) {
    @Transactional(readOnly = true)
    fun validateExistsUser(id: Long) {
        if (userRepository.existsById(id).not()) {
            throw EntityNotFoundException(DomainStatus.USER_NOT_FOUND)
        }
    }

    @Transactional
    fun create(command: UserCommand.Create): UserResult {
        if (userRepository.existsByUuid(command.uuid)) {
            throw BadCommandException(USER_ALREADY_EXISTS)
        }

        val user = User(
            name = command.name,
            uuid = command.uuid,
            email = command.email,
            gender = command.gender,
            age = command.age,
            weight = command.weight,
            height = command.height,
            goalType = command.goalType,
            activityLevel = command.activityLevel,
            smi = command.smi,
            fatPercentage = command.fatPercentage,
            targetWeight = command.targetWeight,
            targetCalorie = command.targetCalorie,
            targetSmi = command.targetSmi,
            targetFatPercentage = command.targetFatPercentage,
            targetCarbohydrates = command.targetCarbohydrates,
            targetProtein = command.targetProtein,
            targetFat = command.targetFat,
            providerId = command.providerId,
            providerType = command.providerType
        )
        return userRepository.save(user)
            .let { UserResult.from(it) }
    }


    private fun findById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { EntityNotFoundException(DomainStatus.USER_NOT_FOUND) }
    }

    @Transactional(readOnly = true)
    fun findByUuid(uuid: String): User {
        return userRepository.findByUuid(uuid)
            ?: throw EntityNotFoundException(DomainStatus.USER_NOT_FOUND)
    }

    @Transactional
    fun update(command: UserCommand.Update): UserResult {
        val user = findById(command.id)
        user.apply {
            command.name.let { name = it }
            command.email?.let { email = it }
            command.gender.let { gender = it }
            command.age.let { age = it }
            command.weight.let { weight = it }
            command.height.let { height = it }
            command.goalType.let { goalType = it }
            command.activityLevel?.let { activityLevel = it }
            command.smi?.let { smi = it }
            command.fatPercentage?.let { fatPercentage = it }
            command.targetWeight?.let { targetWeight = it }
            command.targetCalorie?.let { targetCalorie = it }
            command.targetSmi?.let { targetSmi = it }
            command.targetFatPercentage?.let { targetFatPercentage = it }
            command.targetCarbohydrates?.let { targetCarbohydrates = it }
            command.targetProtein?.let { targetProtein = it }
            command.targetFat?.let { targetFat = it }
        }
        return userRepository.save(user)
            .let { UserResult.from(it) }
    }
}
