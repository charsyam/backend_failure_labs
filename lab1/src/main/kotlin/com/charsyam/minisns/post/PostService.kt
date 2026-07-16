package com.charsyam.minisns.post

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository,
) {
    @Transactional
    fun create(request: CreatePostRequest): PostResponse {
        val post = postRepository.save(
            Post(
                title = request.title,
                content = request.content,
            ),
        )
        return PostResponse.from(post)
    }

    @Transactional(readOnly = true)
    fun get(postId: Long): PostResponse = PostResponse.from(findPost(postId))

    @Transactional
    fun increaseLikes(postId: Long): PostResponse {
        val post = findPost(postId)
        post.increaseLikes()
        return PostResponse.from(post)
    }

    @Transactional
    fun increaseComments(postId: Long): PostResponse {
        val post = findPost(postId)
        post.increaseComments()
        return PostResponse.from(post)
    }

    @Transactional
    fun reset(): PostResponse {
        postRepository.deleteAllInBatch()
        val post = postRepository.save(
            Post(
                title = "Lost Update 실습 게시글",
                content = "좋아요와 댓글 수를 동시에 증가시켜 봅니다.",
            ),
        )
        return PostResponse.from(post)
    }

    private fun findPost(postId: Long): Post = postRepository.findById(postId)
        .orElseThrow { PostNotFoundException(postId) }
}

data class CreatePostRequest(
    val title: String,
    val content: String,
)

data class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val likes: Long,
    val comments: Long,
) {
    companion object {
        fun from(post: Post): PostResponse = PostResponse(
            id = requireNotNull(post.id),
            title = post.title,
            content = post.content,
            likes = post.likes,
            comments = post.comments,
        )
    }
}

class PostNotFoundException(postId: Long) : RuntimeException("Post not found: $postId")
