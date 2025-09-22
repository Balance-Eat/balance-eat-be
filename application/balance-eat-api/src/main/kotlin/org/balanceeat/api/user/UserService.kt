package org.balanceeat.api.user

import org.balanceeat.apibase.ApplicationStatus
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.domain.user.UserCommand
import org.balanceeat.domain.user.UserDomainService
import org.balanceeat.domain.user.UserDto
import org.balanceeat.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userDomainService: UserDomainService,
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(request: UserV1Request.Create): UserV1Response.Details {
        val command = UserCommand.Create(
            uuid = request.uuid,
            name = request.name,
            email = request.email,
            gender = request.gender,
            age = request.age,
            height = request.height,
            weight = request.weight,
            goalType = request.goalType,
            activityLevel = request.activityLevel,
            smi = request.smi,
            fatPercentage = request.fatPercentage,
            targetWeight = request.targetWeight,
            targetCalorie = request.targetCalorie,
            targetSmi = request.targetSmi,
            targetFatPercentage = request.targetFatPercentage,
            targetCarbohydrates = request.targetCarbohydrates,
            targetProtein = request.targetProtein,
            targetFat = request.targetFat,
            providerId = request.providerId,
            providerType = request.providerType,
        )
        return userDomainService.create(command)
            .let { UserV1Response.Details.from(it) }
    }

    @Transactional
    fun update(id: Long, request: UserV1Request.Update): UserV1Response.Details {
        val command = UserCommand.Update(
            id = id,
            name = request.name,
            email = request.email,
            gender = request.gender,
            age = request.age,
            height = request.height,
            weight = request.weight,
            goalType = request.goalType,
            activityLevel = request.activityLevel,
            smi = request.smi,
            fatPercentage = request.fatPercentage,
            targetWeight = request.targetWeight,
            targetCalorie = request.targetCalorie,
            targetSmi = request.targetSmi,
            targetFatPercentage = request.targetFatPercentage,
            targetCarbohydrates = request.targetCarbohydrates,
            targetProtein = request.targetProtein,
            targetFat = request.targetFat
        )

        return userDomainService.update(command)
            .let { UserV1Response.Details.from(it) }
    }

    @Transactional(readOnly = true)
    fun findByUuid(uuid: String): UserV1Response.Details {
        val user = userRepository.findByUuid(uuid)
            ?.let { UserDto.from(it) }
            ?: throw NotFoundException(ApplicationStatus.USER_NOT_FOUND)

        return UserV1Response.Details.from(user)
    }
}