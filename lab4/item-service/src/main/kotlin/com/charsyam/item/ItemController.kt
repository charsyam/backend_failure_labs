package com.charsyam.item

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/items")
class ItemController(
    private val itemService: ItemService,
    private val itemRepository: ItemRepository,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestParam(defaultValue = "sample-item") name: String,
    ): ItemResponse = itemService.create(name)

    @GetMapping("/{itemId}")
    fun get(@PathVariable itemId: Long): ItemResponse =
        itemRepository.findById(itemId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Item $itemId does not exist")
        }.toResponse()
}
