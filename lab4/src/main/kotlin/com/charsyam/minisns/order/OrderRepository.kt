package com.charsyam.minisns.order

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OrderRepository : JpaRepository<Order, Long> {
    /**
     * amount 가 기준보다 작은 주문의 note 길이 합을 구한다.
     * 예전에 이 조회가 느리다는 이유로 amount 인덱스를 강제(FORCE INDEX)하도록 튜닝해 두었다.
     */
    @Query(
        value = """
            SELECT COALESCE(SUM(CHAR_LENGTH(o.note)), 0)
            FROM orders o FORCE INDEX (idx_orders_amount)
            WHERE o.amount < :amountLessThan
        """,
        nativeQuery = true,
    )
    fun sumNoteLengthUnderAmount(amountLessThan: Long): Long
}
