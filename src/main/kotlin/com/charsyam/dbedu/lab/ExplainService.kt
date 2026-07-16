package com.charsyam.dbedu.lab

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class ExplainService(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun explain(sql: String, vararg args: Any): List<Map<String, Any?>> =
        jdbcTemplate.queryForList("EXPLAIN $sql", *args)

    fun explainAnalyze(sql: String, vararg args: Any): List<Map<String, Any?>> =
        jdbcTemplate.queryForList("EXPLAIN ANALYZE $sql", *args)

    fun update(sql: String, vararg args: Any): Int =
        jdbcTemplate.update(sql, *args)

    fun queryForLong(sql: String, vararg args: Any): Long =
        jdbcTemplate.queryForObject(sql, Long::class.java, *args) ?: 0L

    fun query(sql: String, vararg args: Any): List<Map<String, Any?>> =
        jdbcTemplate.queryForList(sql, *args)
}
