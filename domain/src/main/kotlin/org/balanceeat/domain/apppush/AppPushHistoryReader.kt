package org.balanceeat.domain.apppush

import org.balanceeat.domain.common.BaseReader
import org.balanceeat.domain.common.DomainStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class AppPushHistoryReader(
    private val appPushHistoryRepository: AppPushHistoryRepository
) : BaseReader<AppPushHistory, AppPushHistoryResult>(appPushHistoryRepository, AppPushHistoryResult) {

    override fun findByIdOrThrow(id: Long): AppPushHistoryResult {
        return findByIdOrThrow(id, DomainStatus.APP_PUSH_HISTORY_NOT_FOUND)
    }

    fun findAllByUserId(userId: Long): List<AppPushHistoryResult> {
        return appPushHistoryRepository.findByUserId(userId)
            .map { AppPushHistoryResult.from(it) }
    }

    fun findAllByDeviceId(deviceId: Long): List<AppPushHistoryResult> {
        return appPushHistoryRepository.findByDeviceId(deviceId)
            .map { AppPushHistoryResult.from(it) }
    }
}
