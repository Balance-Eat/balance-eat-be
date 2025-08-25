package org.balanceeat.domain.user

import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.DomainStatus.USER_ALREADY_EXISTS
import org.balanceeat.domain.common.exceptions.BadRequestException
import org.balanceeat.domain.common.exceptions.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDomainService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(command: UserCommand.Create) {
        if (userRepository.existsByUuid(command.uuid)) {
            throw BadRequestException(USER_ALREADY_EXISTS)
        }

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
            targetCarbohydrates = command.targetCarbohydrates,
            targetProtein = command.targetProtein,
            targetFat = command.targetFat,
            providerId = command.providerId,
            providerType = command.providerType
        )
        userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { NotFoundException(DomainStatus.USER_NOT_FOUND) }
    }

    @Transactional(readOnly = true)
    fun findByUuid(uuid: String): User {
        return userRepository.findByUuid(uuid)
            ?: throw NotFoundException(DomainStatus.USER_NOT_FOUND)
    }

    @Transactional
    fun update(id: Long, command: UserCommand.Update) {
        val user = findById(id)
        user.apply {
            command.name?.let { name = it }
            command.email?.let { email = it }
            command.gender?.let { gender = it }
            command.age?.let { age = it }
            command.weight?.let { weight = it }
            command.height?.let { height = it }
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
        userRepository.save(user)
    }
}
