package com.charsyam.item

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SearchClient(
    builder: RestClient.Builder,
    @Value("\${search-service.base-url}") baseUrl: String,
) {
    private val client = builder.baseUrl(baseUrl).build()

    @Async
    fun requestIndex(itemId: Long) {
        client.post()
            .uri("/indexes")
            .body(IndexRequest(itemId))
            .retrieve()
            .toBodilessEntity()
    }
}

data class IndexRequest(val itemId: Long)
