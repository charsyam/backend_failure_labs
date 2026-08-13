package com.charsyam.search

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue
import org.springframework.stereotype.Component

@Component
class IndexResultStore {
    private val results = ConcurrentLinkedQueue<IndexResult>()

    fun add(result: IndexResult) = results.add(result)
    fun findAll(): List<IndexResult> = results.toList()
    fun clear() = results.clear()
}

data class IndexResult(
    val itemId: Long,
    val success: Boolean,
    val httpStatus: Int,
    val response: String?,
    val callbackAt: LocalDateTime,
)
