package com.charsyam.dbedu.lab

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/labs/5-transaction-visibility")
class Lab5TransactionVisibilityController(
    private val service: Lab5TransactionVisibilityService,
    private val partnerOrderQueue: PartnerOrderQueue,
) {
    @PostMapping("/bad")
    fun bad(
        @RequestParam(defaultValue = "1") userId: Long,
        @RequestParam(defaultValue = "1000") sleepBeforeCommitMs: Long,
    ): VisibilityResponse = service.badCallExternalBeforeCommit(userId, sleepBeforeCommitMs)

    @GetMapping("/partner-attempts")
    fun partnerAttempts(): List<PartnerFetchAttempt> = partnerOrderQueue.attempts()
}
