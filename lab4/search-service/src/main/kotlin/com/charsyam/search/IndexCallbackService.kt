package com.charsyam.search

import org.springframework.stereotype.Service

@Service
class IndexCallbackService(
    private val asyncItemClient: AsyncItemClient,
) {
    fun callback(request: IndexRequest) {
        asyncItemClient.get(request.itemId)
    }
}
