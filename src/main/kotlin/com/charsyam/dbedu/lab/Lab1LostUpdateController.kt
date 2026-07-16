package com.charsyam.dbedu.lab

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/labs/1-lost-update")
class Lab1LostUpdateController(
    private val service: Lab1LostUpdateService,
) {
    @PostMapping("/reset")
    fun reset(): UserStateResponse = service.reset()

    @GetMapping("/users/{id}")
    fun get(@PathVariable id: Long): UserStateResponse = service.get(id)

    @PostMapping("/users/{id}/unsafe-likes")
    fun unsafeLikes(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "1") delta: Int,
    ): UserStateResponse = service.unsafeIncreaseLikes(id, delta)

    @PostMapping("/users/{id}/unsafe-comments")
    fun unsafeComments(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "1") delta: Int,
    ): UserStateResponse = service.unsafeIncreaseComments(id, delta)
}
