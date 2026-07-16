package com.charsyam.dbedu.lab

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/labs/4-optimizer")
class Lab4OptimizerController(
    private val orders: LabOrderDataService,
    private val explain: ExplainService,
) {
    @PostMapping("/seed-selective")
    fun seedSelective(@RequestParam(defaultValue = "100000") rows: Int): Map<String, Any> {
        val result = orders.seedOrdersForAmountSelectivity(rows, lowAmountRatioPercent = 5)
        orders.ensureAmountIndex()
        orders.analyzeOrders()
        return result + ("amountIndex" to "created")
    }

    @PostMapping("/seed-low-selectivity")
    fun seedLowSelectivity(@RequestParam(defaultValue = "100000") rows: Int): Map<String, Any> {
        val result = orders.seedOrdersForAmountSelectivity(rows, lowAmountRatioPercent = 95)
        orders.ensureAmountIndex()
        orders.analyzeOrders()
        return result + ("amountIndex" to "created")
    }

    @PostMapping("/explain")
    fun explainByAmount(@RequestParam(defaultValue = "500") amountLessThan: Int): List<Map<String, Any?>> =
        explain.explain("select * from orders where amount < ?", amountLessThan)

    @PostMapping("/explain-analyze")
    fun explainAnalyzeByAmount(@RequestParam(defaultValue = "500") amountLessThan: Int): List<Map<String, Any?>> =
        explain.explainAnalyze("select * from orders where amount < ?", amountLessThan)
}
