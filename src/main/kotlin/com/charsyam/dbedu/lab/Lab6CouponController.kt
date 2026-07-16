package com.charsyam.dbedu.lab

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/labs/6-check-then-insert")
class Lab6CouponController(
    private val service: Lab6CouponService,
) {
    @DeleteMapping("/coupons")
    fun clear(): Map<String, Long> = mapOf("deleted" to service.clear())

    @PostMapping("/unsafe")
    fun unsafe(
        @RequestParam(defaultValue = "1") userId: Long,
        @RequestParam(defaultValue = "100") policyId: Long,
        @RequestParam(defaultValue = "1000") sleepMs: Long,
    ): CouponResponse = service.unsafeIssue(userId, policyId, sleepMs)
}
