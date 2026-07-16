package com.charsyam.minisns.order

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

/**
 * search 조회의 실행계획을 조회한다.
 * 애플리케이션이 실제로 실행하는 쿼리 그대로 EXPLAIN 을 떠서, 어떤 접근 방식이
 * 선택되는지 관찰할 수 있게 한다.
 */
@Repository
class OrderJdbcRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun explain(amountLessThan: Long): String =
        runExplain("EXPLAIN", amountLessThan)

    fun explainAnalyze(amountLessThan: Long): String =
        runExplain("EXPLAIN ANALYZE", amountLessThan)

    private fun runExplain(prefix: String, amountLessThan: Long): String {
        val sql =
            "$prefix SELECT * FROM orders WHERE amount < ? ORDER BY id LIMIT 20"
        val rows = jdbcTemplate.queryForList(sql, amountLessThan)
        return rows.joinToString("\n") { row ->
            row.values.joinToString(" | ") { it?.toString() ?: "NULL" }
        }
    }
}
