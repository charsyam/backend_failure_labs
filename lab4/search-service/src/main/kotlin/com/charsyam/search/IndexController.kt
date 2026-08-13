package com.charsyam.search

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/indexes")
class IndexController(
    private val callbackService: IndexCallbackService,
    private val resultStore: IndexResultStore,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun requestIndex(@RequestBody request: IndexRequest): ResponseEntity<IndexAcceptedResponse> {
        callbackService.callback(request)
        log.info("index request accepted: itemId={}", request.itemId)
        return ResponseEntity.accepted().body(IndexAcceptedResponse(request.itemId))
    }

    @GetMapping("/results")
    fun results(): List<IndexResult> = resultStore.findAll()

    @DeleteMapping("/results")
    fun clear(): ResponseEntity<Void> {
        resultStore.clear()
        return ResponseEntity.noContent().build()
    }
}

data class IndexRequest(val itemId: Long)
data class IndexAcceptedResponse(val itemId: Long)
