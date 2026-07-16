package com.charsyam.minisns.post

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreatePostRequest): PostResponse = postService.create(request)

    @GetMapping("/{postId}")
    fun get(@PathVariable postId: Long): PostResponse = postService.get(postId)

    @PostMapping("/{postId}/likes")
    fun increaseLikes(@PathVariable postId: Long): PostResponse =
        postService.increaseLikes(postId)

    @PostMapping("/{postId}/comments")
    fun increaseComments(@PathVariable postId: Long): PostResponse =
        postService.increaseComments(postId)

    @PostMapping("/reset")
    fun reset(): PostResponse = postService.reset()

    @ExceptionHandler(PostNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(exception: PostNotFoundException): ErrorResponse =
        ErrorResponse(message = requireNotNull(exception.message))
}

data class ErrorResponse(
    val message: String,
)
