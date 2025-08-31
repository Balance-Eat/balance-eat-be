package org.balanceeat.admin.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["org.balanceeat"])
class BalanceEatAdminApplication

fun main(args: Array<String>) {
    runApplication<BalanceEatAdminApplication>(*args)
}