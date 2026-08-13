package com.charsyam.item

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class ItemServiceApplication

fun main(args: Array<String>) {
    runApplication<ItemServiceApplication>(*args)
}
