package com.charsyam.lab5

import com.charsyam.lab5.payment.PaymentBatchResponse
import com.charsyam.lab5.payment.PaymentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/lab5")
class Lab5Controller(
    private val lab5Service: Lab5Service,
    private val paymentService: PaymentService,
) {
    @PostMapping("/setup")
    fun setup(
        @RequestParam(defaultValue = "100") orderCount: Int,
        @RequestParam(defaultValue = "100") orderAmount: Long,
        @RequestParam(defaultValue = "100000") walletBalance: Long,
    ): SetupResponse = lab5Service.setup(orderCount, orderAmount, walletBalance)

    @PostMapping("/wallets/{walletId}/pay-all")
    fun payAll(@PathVariable walletId: Long): PaymentBatchResponse = paymentService.payAll(walletId)

    @GetMapping("/wallets/{walletId}/result")
    fun result(@PathVariable walletId: Long): LabResult = lab5Service.result(walletId)
}
