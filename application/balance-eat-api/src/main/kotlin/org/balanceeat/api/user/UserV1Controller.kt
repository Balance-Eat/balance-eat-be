package org.balanceeat.api.user

import jakarta.validation.Valid
import org.balanceeat.api.config.ApiResponse
import org.balanceeat.domain.user.UserCommand
import org.balanceeat.domain.user.UserDomainService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserV1Controller(
    private val userDomainService: UserDomainService,
) : UserV1ApiSpec {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun create(@RequestBody @Valid request: UserV1Request.Create): ApiResponse<Void> {
        val command = UserCommand.Create(
            uuid = request.uuid,
            name = request.name,
            gender = request.gender,
            age = request.age,
            height = request.height,
            weight = request.weight,
            email = request.email,
            activityLevel = request.activityLevel,
            smi = request.smi,
            fatPercentage = request.fatPercentage,
            targetWeight = request.targetWeight,
            targetCalorie = request.targetCalorie,
            targetSmi = request.targetSmi,
            targetFatPercentage = request.targetFatPercentage,
            providerId = request.providerId,
            providerType = request.providerType
        )
        userDomainService.create(command)
        return ApiResponse.success()
    }
}
