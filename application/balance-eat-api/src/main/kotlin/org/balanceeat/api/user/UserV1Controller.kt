package org.balanceeat.api.user

import jakarta.validation.Valid
import org.balanceeat.api.config.ApiResponse
import org.balanceeat.domain.user.UserCommand
import org.balanceeat.domain.user.UserDomainService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/{id}")
    override fun getById(@PathVariable id: Long): ApiResponse<UserV1Response.Info> {
        val user = userDomainService.findById(id)
        val response = UserV1Response.Info(
            id = user.id,
            uuid = user.uuid,
            name = user.name,
            email = user.email,
            gender = user.gender,
            age = user.age,
            weight = user.weight,
            height = user.height,
            activityLevel = user.activityLevel,
            smi = user.smi,
            fatPercentage = user.fatPercentage,
            targetWeight = user.targetWeight,
            targetCalorie = user.targetCalorie,
            targetSmi = user.targetSmi,
            targetFatPercentage = user.targetFatPercentage,
            providerId = user.providerId,
            providerType = user.providerType
        )
        return ApiResponse.success(response)
    }

    @PatchMapping("/{id}")
    override fun update(
        @PathVariable id: Long,
        @RequestBody @Valid request: UserV1Request.Update
    ): ApiResponse<Void> {
        val command = UserCommand.Update(
            name = request.name,
            email = request.email,
            gender = request.gender,
            age = request.age,
            height = request.height,
            weight = request.weight,
            activityLevel = request.activityLevel,
            smi = request.smi,
            fatPercentage = request.fatPercentage,
            targetWeight = request.targetWeight,
            targetCalorie = request.targetCalorie,
            targetSmi = request.targetSmi,
            targetFatPercentage = request.targetFatPercentage
        )
        userDomainService.update(id, command)
        return ApiResponse.success()
    }
}
