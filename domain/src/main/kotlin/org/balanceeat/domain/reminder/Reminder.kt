package org.balanceeat.domain.reminder

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID
import java.time.LocalDateTime

@Entity
@Table(name = "reminder")
class Reminder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = NEW_ID,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(nullable = false, length = 500)
    var content: String,

    @Column(name = "send_datetime", nullable = false)
    var sendDatetime: LocalDateTime
) : BaseEntity() {

    companion object {
        const val MAX_CONTENT_LENGTH = 500
    }

    override fun guard() {
        require(content.isNotBlank()) { "리마인더 내용은 필수값입니다" }
        require(content.length <= MAX_CONTENT_LENGTH) { "리마인더 내용은 ${MAX_CONTENT_LENGTH}자를 초과할 수 없습니다" }
        require(userId > 0) { "사용자 ID는 0보다 큰 값이어야 합니다" }
        require(sendDatetime.second == 0) { "리마인더 전송 시간의 초는 0이어야 합니다" }
    }

    fun update(content: String?, sendDatetime: LocalDateTime?) {
        content?.let { this.content = it }
        sendDatetime?.let { this.sendDatetime = it.withSecond(0).withNano(0) }
    }
}
