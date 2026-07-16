package com.charsyam.dbedu.lab

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.system.measureTimeMillis

@RestController
@RequestMapping("/labs/3-count")
class Lab3CountController(
    private val orders: LabOrderDataService,
    private val explain: ExplainService,
) {
    @PostMapping("/seed")
    fun seed(
        @RequestParam(defaultValue = "100000") rows: Int,
        @RequestParam(defaultValue = "80") paidRatioPercent: Int,
    ): Map<String, Any> {
        val result = orders.seedOrders(rows, paidRatioPercent)
        orders.dropStatusIndex()
        return result + ("statusIndex" to "dropped")
    }

    @PostMapping("/slow-count")
    fun slowCount(): CountResponse {
        var count = 0L
        val elapsed = measureTimeMillis {
            count = explain.queryForLong("select count(*) from orders where status = 'PAID'")
        }
        return CountResponse("Slow Count without status index", count, elapsed, explain.explain("select count(*) from orders where status = 'PAID'"))
    }
}
