package com.charsyam.dbedu.lab

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/labs/2-batch-insert")
class Lab2BatchInsertController(
    private val service: Lab2BatchInsertService,
) {
    @DeleteMapping("/comments")
    fun clear(): Map<String, Long> = mapOf("deleted" to service.clear())

    @PostMapping("/save-loop")
    fun saveLoop(@RequestParam(defaultValue = "1000") rows: Int): BatchResponse = service.saveLoop(rows)
}
