package com.charsyam.search

import java.time.LocalDateTime
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

@Component
class AsyncItemClient(
    builder: RestClient.Builder,
    @Value("\${item-service.base-url}") itemServiceBaseUrl: String,
    private val resultStore: IndexResultStore,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val itemClient = builder.baseUrl(itemServiceBaseUrl).build()

    @Async
    fun get(itemId: Long) {
        val result = runCatching {
            itemClient.get().uri("/items/{id}", itemId).retrieve().toEntity(String::class.java)
        }.fold(
            onSuccess = { IndexResult(itemId, true, it.statusCode.value(), it.body, LocalDateTime.now()) },
            onFailure = { IndexResult(itemId, false, statusOf(it), it.message, LocalDateTime.now()) },
        )
        resultStore.add(result)
        log.info("item get finished: itemId={}, success={}, status={}", itemId, result.success, result.httpStatus)
    }

    private fun statusOf(error: Throwable): Int =
        (error as? RestClientResponseException)?.statusCode?.value() ?: 0
}
