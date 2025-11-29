package org.balanceeat.domain.apppush

import org.balanceeat.common.TestFixture

class AppPushHistoryCommandFixture {

    class Create(
        var deviceId: Long = 1L,
        var title: String = "테스트 푸시 제목",
        var content: String = "테스트 푸시 내용입니다.",
        var deepLink: String? = null
    ) : TestFixture<AppPushHistoryCommand.Create> {
        override fun create(): AppPushHistoryCommand.Create {
            return AppPushHistoryCommand.Create(
                deviceId = deviceId,
                title = title,
                content = content,
                deepLink = deepLink
            )
        }
    }
}

fun appPushHistoryCreateCommandFixture(block: AppPushHistoryCommandFixture.Create.() -> Unit = {}): AppPushHistoryCommand.Create {
    return AppPushHistoryCommandFixture.Create().apply(block).create()
}
