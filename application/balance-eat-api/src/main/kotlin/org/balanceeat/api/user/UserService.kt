package org.balanceeat.api.user

import org.balanceeat.domain.user.UserCommand
import org.balanceeat.domain.user.UserReader
import org.balanceeat.domain.user.UserWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userWriter: UserWriter,
    private val userReader: UserReader
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
        return userWriter.create(command)
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

        return userWriter.update(command)
            .let { UserV1Response.Details.from(it) }
    }

    @Transactional(readOnly = true)
    fun findByUuid(uuid: String): UserV1Response.Details {
        val user = userReader.findByUuidOrThrow(uuid)
        return UserV1Response.Details.from(user)
    }
}