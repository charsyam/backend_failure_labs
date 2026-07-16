package com.charsyam.dbedu.lab

import com.charsyam.dbedu.repository.OrderRepository
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.system.measureTimeMillis

@RestController
@RequestMapping("/labs/7-projection-covering-index")
class Lab7ProjectionController(
    private val orders: LabOrderDataService,
    private val orderRepository: OrderRepository,
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

    @GetMapping("/entity")
    fun entity(
        @RequestParam(defaultValue = "PAID") status: String,
        @RequestParam(defaultValue = "100") size: Int,
    ): Map<String, Any> {
        var rows = emptyList<OrderRowResponse>()
        val elapsed = measureTimeMillis {
            rows = orderRepository.findByStatus(status, PageRequest.of(0, size)).map { it.toRow() }
        }
        return mapOf("elapsedMs" to elapsed, "rows" to rows)
    }
}

private fun com.charsyam.dbedu.domain.OrderEntity.toRow() = OrderRowResponse(
    id = requireNotNull(id),
    userId = userId,
    status = status,
    amount = amount,
    createdAt = createdAt,
)
