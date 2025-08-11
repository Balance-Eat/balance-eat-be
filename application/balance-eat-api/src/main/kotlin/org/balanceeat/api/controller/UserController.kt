package org.balanceeat.api.controller

import org.balanceeat.api.config.ApiResponse
import org.balanceeat.api.dto.UserCreateRequest
import org.balanceeat.api.dto.UserResponse
import org.balanceeat.domain.user.User
import org.balanceeat.domain.user.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    private val userRepository: UserRepository,
) {
    @GetMapping
    fun getAllUsers(): ApiResponse<List<UserResponse>> {
        val users = userRepository.findAll()
        val userResponses = users.map { UserResponse.from(it) }
        return ApiResponse.success(userResponses, "사용자 목록을 조회했습니다")
    }

    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable id: Long,
    ): ApiResponse<UserResponse> {
        val user =
            userRepository.findById(id).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다")
            }
        return ApiResponse.success(UserResponse.from(user), "사용자 정보를 조회했습니다")
    }

    @GetMapping("/uuid/{uuid}")
    fun getUserByUuid(
        @PathVariable uuid: UUID,
    ): ApiResponse<UserResponse> {
        val user =
            userRepository.findByUuid(uuid)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다")
        return ApiResponse.success(UserResponse.from(user), "사용자 정보를 조회했습니다")
    }

    @PostMapping
    fun createUser(
        @RequestBody request: UserCreateRequest,
    ): ApiResponse<UserResponse> {
        if (userRepository.existsByEmail(request.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다")
        }

        val user =
            User(
                name = request.name,
                email = request.email,
                gender = request.gender,
                age = request.age,
                weight = request.weight,
                height = request.height,
                targetWeight = request.targetWeight,
                targetCalorie = request.targetCalorie,
            )

        val savedUser = userRepository.save(user)
        return ApiResponse.success(UserResponse.from(savedUser), "사용자가 생성되었습니다")
    }

    @GetMapping("/search")
    fun searchUsers(
        @RequestParam name: String,
    ): ApiResponse<List<UserResponse>> {
        val users = userRepository.findByNameContaining(name)
        val userResponses = users.map { UserResponse.from(it) }
        return ApiResponse.success(userResponses, "검색 결과를 조회했습니다")
    }

    @DeleteMapping("/{id}")
    fun deleteUser(
        @PathVariable id: Long,
    ): ApiResponse<String> {
        if (!userRepository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다")
        }

        userRepository.deleteById(id)
        return ApiResponse.success("사용자가 삭제되었습니다")
    }
}
