package org.balanceeat.domain.notification

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID

@Entity
@Table(name = "notification_device")
class NotificationDevice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = NEW_ID,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "agent_id", nullable = false, length = 255)
    val agentId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "os_type", nullable = false, length = 20)
    val osType: OsType,

    @Column(name = "device_name", nullable = false, length = 60)
    val deviceName: String,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = false
) : BaseEntity() {

    companion object {
        const val MAX_AGENT_ID_LENGTH = 255
        const val MAX_DEVICE_NAME_LENGTH = 60
    }

    override fun guard() {
        require(userId > 0) { "사용자 ID는 0보다 큰 값이어야 합니다" }
        require(agentId.isNotBlank()) { "Agent ID는 필수값입니다" }
        require(agentId.length <= MAX_AGENT_ID_LENGTH) { "Agent ID는 ${MAX_AGENT_ID_LENGTH}자를 초과할 수 없습니다" }
        require(deviceName.isNotBlank()) { "디바이스명은 필수값입니다" }
        require(deviceName.length <= MAX_DEVICE_NAME_LENGTH) { "디바이스명은 ${MAX_DEVICE_NAME_LENGTH}자를 초과할 수 없습니다" }
    }

    fun update(isActive: Boolean) {
        this.isActive = isActive
    }

    enum class OsType {
        AOS,
        IOS
    }
}
