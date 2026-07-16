package com.charsyam.minisns.order

import kotlin.system.measureNanoTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderJdbcRepository: OrderJdbcRepository,
) {
    /**
     * 주문 한 건을 저장한다. 지극히 평범한 주문 생성 API 다.
     * 이 API 를 amount 가 작은 값(예: 0)으로 반복 호출하면 amount 분포가 서서히 기울어진다.
     */
    @Transactional
    fun createOrder(customer: String, amount: Long): OrderResponse {
        val order = orderRepository.save(
            Order(customer = customer, amount = amount, note = NOTE),
        )
        return OrderResponse(id = requireNotNull(order.id), customer = order.customer, amount = order.amount)
    }

    /**
     * amount < amountLessThan 인 주문을 오래된 순으로 20건 조회하고, 쿼리에 걸린 시간을 함께 돌려준다.
     * 이 조회가 느려지는지 여부를 elapsedMillis 로 관찰한다.
     */
    @Transactional(readOnly = true)
    fun search(amountLessThan: Long): SearchResponse {
        var orders = emptyList<Order>()
        val elapsedNanos = measureNanoTime {
            orders = orderRepository.findCheapOrders(amountLessThan)
        }
        return SearchResponse(
            amountLessThan = amountLessThan,
            returned = orders.size,
            elapsedMillis = elapsedNanos / 1_000_000,
        )
    }

    @Transactional(readOnly = true)
    fun explain(amountLessThan: Long): ExplainResponse {
        val plan = orderJdbcRepository.explain(amountLessThan)
        return ExplainResponse(command = "EXPLAIN", amountLessThan = amountLessThan, plan = plan)
    }

    @Transactional(readOnly = true)
    fun explainAnalyze(amountLessThan: Long): ExplainResponse {
        val plan = orderJdbcRepository.explainAnalyze(amountLessThan)
        return ExplainResponse(command = "EXPLAIN ANALYZE", amountLessThan = amountLessThan, plan = plan)
    }

    companion object {
        // 주문 상세 메모. seed 스크립트가 넣는 note 와 크기를 맞춘다.
        private val NOTE = "order detail ".repeat(16).take(200)
    }
}

data class OrderResponse(
    val id: Long,
    val customer: String,
    val amount: Long,
)

data class SearchResponse(
    val amountLessThan: Long,
    val returned: Int,
    val elapsedMillis: Long,
)

data class ExplainResponse(
    val command: String,
    val amountLessThan: Long,
    val plan: String,
)
