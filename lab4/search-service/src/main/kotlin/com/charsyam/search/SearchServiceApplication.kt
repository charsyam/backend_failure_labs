package com.charsyam.search

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class SearchServiceApplication

fun main(args: Array<String>) {
    runApplication<SearchServiceApplication>(*args)
}
