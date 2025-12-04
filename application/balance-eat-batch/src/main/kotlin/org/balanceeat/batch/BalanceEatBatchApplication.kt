package org.balanceeat.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["org.balanceeat"])
class BalanceEatBatchApplication

fun main(args: Array<String>) {
    runApplication<BalanceEatBatchApplication>(*args)
}