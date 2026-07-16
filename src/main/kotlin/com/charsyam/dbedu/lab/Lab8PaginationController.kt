package com.charsyam.dbedu.lab

import com.charsyam.dbedu.repository.OrderRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.system.measureTimeMillis

@RestController
@RequestMapping("/labs/8-pagination")
class Lab8PaginationController(
    private val orders: LabOrderDataService,
    private val orderRepository: OrderRepository,
    private val explain: ExplainService,
) {
    @PostMapping("/seed")
    fun seed(
        @RequestParam(defaultValue = "200000") rows: Int,
        @RequestParam(defaultValue = "1") userId: Long,
    ): Map<String, Any> = orders.seedOrdersForSingleUser(rows, userId)

    @GetMapping("/offset")
    fun offset(
        @RequestParam(defaultValue = "1") userId: Long,
        @RequestParam(defaultValue = "1000") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): Map<String, Any> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")))
        var rows = emptyList<OrderRowResponse>()
        val elapsed = measureTimeMillis {
            rows = orderRepository.findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable).map { it.toRowForPagination() }
        }
        return mapOf(
            "elapsedMs" to elapsed,
            "rows" to rows,
            "explain" to explain.explain(
                "select * from orders where user_id = ? order by created_at desc, id desc limit $size offset ${page * size}",
                userId,
            ),
        )
    }
}

private fun com.charsyam.dbedu.domain.OrderEntity.toRowForPagination() = OrderRowResponse(
    id = requireNotNull(id),
    userId = userId,
    status = status,
    amount = amount,
    createdAt = createdAt,
)
