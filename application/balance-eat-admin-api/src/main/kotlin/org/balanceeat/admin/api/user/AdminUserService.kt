package org.balanceeat.admin.api.user

import org.balanceeat.domain.user.UserCommand
import org.balanceeat.domain.user.UserDomainService
import org.balanceeat.domain.user.UserDto
import org.springframework.stereotype.Service

@Service
class AdminUserService(
    private val userDomainService: UserDomainService
) {

    fun update(request: AdminUserV1Request.Update, userId: Long, adminId: Long): UserDto {
        val command = UserCommand.UpdateByAdmin(
            id = userId,
            adminId = adminId,
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
            providerType = request.providerType
        )
        return userDomainService.updateByAdmin(command)
    }

    fun getDetails(userId: Long): UserDto {
        return userDomainService.getDetailsForAdmin(userId)
    }
}