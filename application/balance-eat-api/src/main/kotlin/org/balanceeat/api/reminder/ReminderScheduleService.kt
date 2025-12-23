package org.balanceeat.api.reminder

import org.balanceeat.apibase.component.AppPushRequest
import org.balanceeat.apibase.component.AppPushSender
import org.balanceeat.domain.apppush.NotificationDeviceReader
import org.balanceeat.domain.reminder.ReminderReader
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ReminderScheduleService(
    private val reminderReader: ReminderReader,
    private val appPushSender: AppPushSender,
    private val notificationDeviceReader: NotificationDeviceReader
){
    private val CHUNK_SIZE = 200

    fun sendAllBy(targetDateTime: LocalDateTime) {
        val dayOfWeek = targetDateTime.dayOfWeek
        val time = targetDateTime.toLocalTime()

        val totalReminderCount = reminderReader.findAllActiveCountBy(dayOfWeek, time)

        for (offset in 0 until totalReminderCount step CHUNK_SIZE.toLong()) {
            val pageable = PageRequest.of((offset/ CHUNK_SIZE).toInt(), CHUNK_SIZE)
            val reminders = reminderReader.findAllActiveBy(dayOfWeek, time, pageable)
            val notificationDevicesMap = notificationDeviceReader.findAllActiveByUserIds(reminders.map{ it.userId }.toList())
                .groupBy { it.userId }

            reminders.forEach { reminder ->
                val devices = notificationDevicesMap[reminder.userId] ?: return@forEach

                devices.chunked(CHUNK_SIZE).forEach { deviceChunk ->
                    deviceChunk.forEach { device ->
                        appPushSender.sendPushAsync(
                            AppPushRequest.SendMessage(
                                deviceId = device.id,
                                title = "밸런스잇 리마인더",
                                content = reminder.content,
                                deepLink = null
                            )
                        )
                    }
                }
            }
        }
    }
}