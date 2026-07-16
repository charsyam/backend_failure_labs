package com.charsyam.dbedu.lab

import com.charsyam.dbedu.domain.OrderEntity
import com.charsyam.dbedu.repository.OrderRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class LabOrderDataService(
    private val orderRepository: OrderRepository,
    private val jdbcTemplate: JdbcTemplate,
) {
    @Transactional
    fun clearOrders(): Long {
        val count = orderRepository.count()
        orderRepository.deleteAllInBatch()
        return count
    }

    fun seedOrders(rows: Int, paidRatioPercent: Int = 80): Map<String, Any> {
        clearOrders()
        val base = LocalDateTime.now().minusDays(30)
        (1..rows).chunked(1000).forEach { chunk ->
            jdbcTemplate.batchUpdate(
                "insert into orders(user_id, status, amount, created_at) values (?, ?, ?, ?)",
                chunk.map { i ->
                    val status = if (i % 100 < paidRatioPercent) "PAID" else "CANCELLED"
                    arrayOf<Any>(
                        (i % 1000 + 1).toLong(),
                        status,
                        BigDecimal((i % 50000) + 1000).movePointLeft(2),
                        base.plusSeconds(i.toLong()),
                    )
                },
            )
        }
        return mapOf("rows" to rows, "paidRatioPercent" to paidRatioPercent)
    }

    fun seedOrdersForAmountSelectivity(rows: Int, lowAmountRatioPercent: Int): Map<String, Any> {
        clearOrders()
        val base = LocalDateTime.now().minusDays(30)
        (1..rows).chunked(1000).forEach { chunk ->
            jdbcTemplate.batchUpdate(
                "insert into orders(user_id, status, amount, created_at) values (?, ?, ?, ?)",
                chunk.map { i ->
                    val lowAmount = i % 100 < lowAmountRatioPercent
                    arrayOf<Any>(
                        (i % 1000 + 1).toLong(),
                        if (i % 2 == 0) "PAID" else "CANCELLED",
                        if (lowAmount) BigDecimal("100.00") else BigDecimal("1000.00"),
                        base.plusSeconds(i.toLong()),
                    )
                },
            )
        }
        return mapOf("rows" to rows, "lowAmountRatioPercent" to lowAmountRatioPercent)
    }

    fun seedOrdersForSingleUser(rows: Int, userId: Long): Map<String, Any> {
        clearOrders()
        val base = LocalDateTime.now().minusDays(30)
        (1..rows).chunked(1000).forEach { chunk ->
            jdbcTemplate.batchUpdate(
                "insert into orders(user_id, status, amount, created_at) values (?, ?, ?, ?)",
                chunk.map { i ->
                    arrayOf<Any>(
                        userId,
                        if (i % 2 == 0) "PAID" else "CANCELLED",
                        BigDecimal((i % 50000) + 1000).movePointLeft(2),
                        base.plusSeconds(i.toLong()),
                    )
                },
            )
        }
        return mapOf("rows" to rows, "userId" to userId)
    }

    fun ensureStatusIndex() {
        if (!indexExists("idx_orders_status")) {
            jdbcTemplate.execute("create index idx_orders_status on orders(status)")
        }
    }

    fun ensureAmountIndex() {
        if (!indexExists("idx_orders_amount")) {
            jdbcTemplate.execute("create index idx_orders_amount on orders(amount)")
        }
    }

    fun dropStatusIndex() {
        if (indexExists("idx_orders_status")) {
            jdbcTemplate.execute("drop index idx_orders_status on orders")
        }
    }

    fun analyzeOrders() {
        jdbcTemplate.execute("analyze table orders")
    }

    @Transactional
    fun createOrder(userId: Long, status: String = "CREATED", amount: BigDecimal = BigDecimal("100.00")): OrderEntity =
        orderRepository.save(OrderEntity(userId = userId, status = status, amount = amount))

    private fun indexExists(indexName: String): Boolean =
        jdbcTemplate.queryForObject(
            """
            select count(*)
            from information_schema.statistics
            where table_schema = database()
              and table_name = 'orders'
              and index_name = ?
            """.trimIndent(),
            Long::class.java,
            indexName,
        ) != 0L
}
