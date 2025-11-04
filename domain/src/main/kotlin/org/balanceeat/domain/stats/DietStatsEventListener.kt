package org.balanceeat.domain.stats

import org.balanceeat.domain.diet.DietChangedEvent
import org.balanceeat.domain.diet.DietDeletedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class DietStatsEventListener(
    private val dietStatsDomainService: DietStatsDomainService
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleDietChangedEvent(event: DietChangedEvent) {
        dietStatsDomainService.upsert(event.dietId)
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleDietDeletedEvent(event: DietDeletedEvent) {
        dietStatsDomainService.upsert(event.dietId)
    }
}