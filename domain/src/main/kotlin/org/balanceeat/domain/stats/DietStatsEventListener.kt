package org.balanceeat.domain.stats

import org.balanceeat.domain.diet.DietChangedEvent
import org.balanceeat.domain.diet.DietDeletedEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class DietStatsEventListener(
    private val dietStatsDomainService: DietStatsDomainService
) {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleDietChangedEvent(event: DietChangedEvent) {
        dietStatsDomainService.upsert(event.dietId)
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleDietDeletedEvent(event: DietDeletedEvent) {
        val (diet) = event
        dietStatsDomainService.upsert(diet.userId, diet.consumedAt.toLocalDate())
    }
}