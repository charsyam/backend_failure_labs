package com.charsyam.minisns.post

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService,
) {
    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAll(
        @RequestParam(defaultValue = "1000") count: Int,
    ): BulkCreatePostResponse = postService.createAll(count)

    @GetMapping
    fun getPosts(
        @RequestParam(defaultValue = "20") limit: Int,
    ): List<PostResponse> = postService.getPosts(limit)

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(exception: IllegalArgumentException): ErrorResponse =
        ErrorResponse(message = requireNotNull(exception.message))
}

data class ErrorResponse(
    val message: String,
)
