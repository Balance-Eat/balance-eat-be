package org.balanceeat.api.notification

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.balanceeat.domain.notification.NotificationDevice
import org.balanceeat.domain.notification.NotificationDeviceResult

class NotificationDeviceV1Request {
    data class Create(
        @field:NotBlank(message = "agentId는 필수입니다")
        val agentId: String,

        @field:NotNull(message = "osType은 필수입니다")
        val osType: NotificationDevice.OsType,

        @field:NotBlank(message = "deviceName은 필수입니다")
        val deviceName: String,

        @field:NotNull(message = "isActive는 필수입니다")
        val isActive: Boolean
    )

    data class UpdateActivation(
        @field:NotNull(message = "isActive는 필수입니다")
        val isActive: Boolean
    )
}

class NotificationDeviceV1Response {
    data class Details(
        val id: Long,
        val userId: Long,
        val agentId: String,
        val osType: NotificationDevice.OsType,
        val deviceName: String,
        val isActive: Boolean
    ) {
        companion object {
            fun from(result: NotificationDeviceResult) = Details(
                id = result.id,
                userId = result.userId,
                agentId = result.agentId,
                osType = result.osType,
                deviceName = result.deviceName,
                isActive = result.isActive
            )
        }
    }
}
