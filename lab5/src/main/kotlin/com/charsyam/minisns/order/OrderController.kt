package com.charsyam.minisns.order

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(
        @RequestParam(defaultValue = "buyer") customer: String,
        @RequestParam(defaultValue = "0") amount: Long,
    ): OrderResponse = orderService.createOrder(customer, amount)

    @GetMapping("/search")
    fun search(
        @RequestParam(defaultValue = "500") amountLessThan: Long,
    ): SearchResponse = orderService.search(amountLessThan)

    @PostMapping("/explain")
    fun explain(
        @RequestParam(defaultValue = "500") amountLessThan: Long,
    ): ExplainResponse = orderService.explain(amountLessThan)

    @PostMapping("/explain-analyze")
    fun explainAnalyze(
        @RequestParam(defaultValue = "500") amountLessThan: Long,
    ): ExplainResponse = orderService.explainAnalyze(amountLessThan)

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(exception: IllegalArgumentException): ErrorResponse =
        ErrorResponse(message = requireNotNull(exception.message))
}

data class ErrorResponse(
    val message: String,
)
