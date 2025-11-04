package org.balanceeat.domain.diet

interface DietChangedEvent{
    val dietId: Long
}

data class DietCreatedEvent(
    override val dietId: Long
) : DietChangedEvent

data class DietUpdatedEvent(
    override val dietId: Long
) : DietChangedEvent

data class DietDeletedEvent(
    val dietId: Long
)


