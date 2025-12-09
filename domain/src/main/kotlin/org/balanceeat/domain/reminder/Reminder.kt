package org.balanceeat.domain.reminder

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID
import org.hibernate.annotations.Type
import java.time.DayOfWeek
import java.time.LocalTime

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

    @Column(name = "send_time", nullable = false)
    var sendTime: LocalTime,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "day_of_weeks", nullable = false, columnDefinition = "jsonb")
    @Type(JsonType::class)
    var dayOfWeeks: List<DayOfWeek>
) : BaseEntity() {

    companion object {
        const val MAX_CONTENT_LENGTH = 500
    }

    override fun guard() {
        require(content.isNotBlank()) { "리마인더 내용은 필수값입니다" }
        require(content.length <= MAX_CONTENT_LENGTH) { "리마인더 내용은 ${MAX_CONTENT_LENGTH}자를 초과할 수 없습니다" }
        require(userId > 0) { "사용자 ID는 0보다 큰 값이어야 합니다" }
        require(sendTime.second == 0) { "리마인더 전송 시간의 초는 0이어야 합니다" }
        require(sendTime.minute % 5 == 0) { "리마인더 전송 시간의 분은 5분 단위로만 설정할 수 있습니다" }
        require(dayOfWeeks.isNotEmpty()) { "알림 요일은 최소 1개 이상 선택해야 합니다" }
        require(dayOfWeeks.size == dayOfWeeks.distinct().size) { "중복된 요일은 선택할 수 없습니다" }
    }

    fun update(
        content: String,
        sendTime: LocalTime,
        isActive: Boolean,
        dayOfWeeks: List<DayOfWeek>
    ) {
        this.content = content
        this.sendTime = sendTime.withSecond(0)
        this.isActive = isActive
        this.dayOfWeeks = dayOfWeeks
        guard()
    }

    fun updateActivation(isActive: Boolean) {
        this.isActive = isActive
    }
}
