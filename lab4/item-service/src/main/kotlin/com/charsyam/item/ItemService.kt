package com.charsyam.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val searchClient: SearchClient,
) {
    @Transactional
    fun create(name: String): ItemResponse {
        val item = itemRepository.save(Item(name))
        searchClient.requestIndex(requireNotNull(item.id))
        return item.toResponse()
    }
}

data class ItemResponse(val id: Long, val name: String)

fun Item.toResponse() = ItemResponse(requireNotNull(id), name)
