package com.charsyam.minisns.order

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OrderRepository : JpaRepository<Order, Long> {
    /**
     * amount 가 기준보다 작은 주문을 오래된 순(id 오름차순)으로 20건 조회한다.
     * 소액 주문 목록의 첫 페이지를 보여주는 흔한 조회다.
     */
    @Query(
        value = """
            SELECT * FROM orders
            WHERE amount < :amountLessThan
            ORDER BY id
            LIMIT 20
        """,
        nativeQuery = true,
    )
    fun findCheapOrders(amountLessThan: Long): List<Order>
}
