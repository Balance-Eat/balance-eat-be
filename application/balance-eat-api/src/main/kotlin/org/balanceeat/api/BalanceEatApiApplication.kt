package org.balanceeat.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["org.balanceeat"])
class BalanceEatApiApplication

fun main(args: Array<String>) {
    runApplication<BalanceEatApiApplication>(*args)
}
