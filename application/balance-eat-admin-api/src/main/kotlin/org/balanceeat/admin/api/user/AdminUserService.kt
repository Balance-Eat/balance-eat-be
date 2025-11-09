package org.balanceeat.admin.api.user

import org.balanceeat.apibase.ApplicationStatus
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.domain.user.UserCommand
import org.balanceeat.domain.user.UserDomainService
import org.balanceeat.domain.user.UserDto
import org.balanceeat.domain.user.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminUserService(
    private val userDomainService: UserDomainService,
    private val userRepository: UserRepository
) {

    @Transactional
    fun update(request: AdminUserV1Request.Update, userId: Long): AdminUserV1Response.Details {
        val command = UserCommand.Update(
            id = userId,
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
            .let { AdminUserV1Response.Details.from(it) }
    }

    @Transactional(readOnly = true)
    fun getDetails(userId: Long): AdminUserV1Response.Details {
        return  userRepository.findByIdOrNull(userId)
            ?.let { UserDto.from(it) }
            ?.let { AdminUserV1Response.Details.from(it) }
            ?: throw BadRequestException(ApplicationStatus.USER_NOT_FOUND)
    }
}