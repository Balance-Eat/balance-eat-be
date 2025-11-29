package org.balanceeat.domain.apppush

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID

@Entity
@Table(name = "app_push_history")
class AppPushHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = NEW_ID,

    @Column(name = "device_id", nullable = false)
    val deviceId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "agent_id", nullable = false, length = 255)
    val agentId: String,

    @Column(name = "title", nullable = false, length = 255)
    val title: String,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Column(name = "deep_link", nullable = true, length = 100)
    val deepLink: String? = null
) : BaseEntity() {
    companion object {
        const val MAX_AGENT_ID_LENGTH = 255
        const val MAX_TITLE_LENGTH = 255
        const val MAX_CONTENT_LENGTH = 10000
        const val MAX_DEEP_LINK_LENGTH = 100
    }

    override fun guard() {
        require(deviceId > 0) { "디바이스 ID는 0보다 큰 값이어야 합니다" }
        require(userId > 0) { "사용자 ID는 0보다 큰 값이어야 합니다" }
        require(agentId.isNotBlank()) { "Agent ID는 필수값입니다" }
        require(agentId.length <= MAX_AGENT_ID_LENGTH) { "Agent ID는 ${MAX_AGENT_ID_LENGTH}자를 초과할 수 없습니다" }
        require(title.isNotBlank()) { "제목은 필수값입니다" }
        require(title.length <= MAX_TITLE_LENGTH) { "제목은 ${MAX_TITLE_LENGTH}자를 초과할 수 없습니다" }
        require(content.isNotBlank()) { "내용은 필수값입니다" }
        require(content.length <= MAX_CONTENT_LENGTH) { "내용은 ${MAX_CONTENT_LENGTH}자를 초과할 수 없습니다" }

        deepLink?.let {
            require(it.length <= MAX_DEEP_LINK_LENGTH) {
                "딥링크는 ${MAX_DEEP_LINK_LENGTH}자를 초과할 수 없습니다"
            }
        }
    }
}
