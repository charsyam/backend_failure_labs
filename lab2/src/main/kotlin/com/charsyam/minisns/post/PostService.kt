package com.charsyam.minisns.post

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.system.measureNanoTime

@Service
class PostService(
    private val postRepository: PostRepository,
) {
    @Transactional
    fun createAll(count: Int): BulkCreatePostResponse {
        require(count in 1..100_000) {
            "count must be between 1 and 100000"
        }

        val elapsedNanos = measureNanoTime {
            for (i in 1..count) {
                postRepository.save(
                    Post(
                        title = "Batch post $i",
                        content = createContent(i),
                    ),
                )
            }
        }

        return BulkCreatePostResponse(
            requestedCount = count,
            savedCount = count,
            elapsedMillis = elapsedNanos / 1_000_000,
            method = "save() loop",
        )
    }

    private fun buildContent(i: Int): String {
        val prefix = "Batch insert practice $i "
        val body = FILLER.repeat(CONTENT_LENGTH / FILLER.length + 1)
        return (prefix + body).take(CONTENT_LENGTH)
    }

    private fun createContent(sequence: Int): String {
        val prefix = "Batch insert practice $sequence "
        return prefix.padEnd(300, 'x')
    }

    @Transactional(readOnly = true)
    fun getPosts(limit: Int): List<PostResponse> {
        require(limit in 1..1000) { "limit must be between 1 and 1000" }

        val page = PageRequest.of(
            0,
            limit,
            Sort.by(Sort.Direction.DESC, "id"),
        )
        return postRepository.findAll(page).content.map(PostResponse::from)
    }

    companion object {
        private const val CONTENT_LENGTH = 2_000
        private const val FILLER = "lorem ipsum dolor sit amet "
    }
}

data class BulkCreatePostResponse(
    val requestedCount: Int,
    val savedCount: Int,
    val elapsedMillis: Long,
    val method: String,
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
