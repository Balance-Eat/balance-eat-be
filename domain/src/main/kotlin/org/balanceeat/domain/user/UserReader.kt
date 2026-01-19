package org.balanceeat.domain.user

import org.balanceeat.domain.common.BaseReader
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class UserReader(
    private val userRepository: UserRepository,
) : BaseReader<User, UserResult>(userRepository, UserResult) {
    override fun findByIdOrThrow(id: Long): UserResult = findByIdOrThrow(id, DomainStatus.USER_NOT_FOUND)

    fun findByUuidOrThrow(uuid: String): UserResult = userRepository
        .findByUuid(uuid)
            ?.let { UserResult.from(it) }
            ?: throw EntityNotFoundException(DomainStatus.USER_NOT_FOUND)

    fun validateExistsUser(id: Long) {
        if (userRepository.existsById(id).not()) {
            throw EntityNotFoundException(DomainStatus.USER_NOT_FOUND)
        }
    }
}
