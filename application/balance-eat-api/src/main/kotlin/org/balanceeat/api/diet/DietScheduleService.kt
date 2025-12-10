package org.balanceeat.api.diet

import org.balanceeat.apibase.ApplicationStatus.INVALID_PARAMETER
import org.balanceeat.apibase.component.AppPushRequest
import org.balanceeat.apibase.component.AppPushSender
import org.balanceeat.apibase.exception.InternalErrorException
import org.balanceeat.domain.apppush.NotificationDeviceReader
import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.diet.DietReader
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

@Service
class DietScheduleService(
    private val dietReader: DietReader,
    private val appPushSender: AppPushSender,
    private val notificationDeviceReader: NotificationDeviceReader
) {
    private val CHUNK_SIZE = 200

    fun sendNotRegisteredDietNotifications(mealType: Diet.MealType, targetDate: LocalDate) {
        val userIds = dietReader.findUserIdsWithoutDietForMealOnDate(mealType, targetDate)
        val mealTypeName = getMealTypeName(mealType)
        val notificationContent = "아직 식단을 등록하지 않으셨군요! 오늘 ${mealTypeName} 식단을 등록해 주세요."

        userIds.chunked(CHUNK_SIZE).forEach { userIdChunk ->
            val notificationDevicesMap = notificationDeviceReader.findAllActiveByUserIds(userIdChunk)
                .groupBy { it.userId }

            userIdChunk.forEach { userId ->
                val devices = notificationDevicesMap[userId] ?: return@forEach

                devices.forEach { device ->
                    appPushSender.sendPushAsync(
                        AppPushRequest.SendMessage(
                            deviceId = device.id,
                            title = "밸런스잇 식단 알림",
                            content = notificationContent,
                            deepLink = null
                        )
                    )
                }
            }
        }
    }

    private fun getMealTypeName(mealType: Diet.MealType): String {
        return when (mealType) {
            Diet.MealType.BREAKFAST -> "아침"
            Diet.MealType.LUNCH -> "점심"
            Diet.MealType.DINNER -> "저녁"
            else -> throw InternalErrorException(INVALID_PARAMETER, "해당 식사 유형은 알림에 적합하지 않습니다.")
        }
    }
}