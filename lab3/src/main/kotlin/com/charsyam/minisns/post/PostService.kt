package com.charsyam.minisns.post

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository,
) {
    @Transactional(readOnly = true)
    fun getPage(page: Int, size: Int): PostPageResponse {
        require(page >= 0) { "page must be greater than or equal to 0" }
        require(size in 1..100) { "size must be between 1 and 100" }

        val pageable = PageRequest.of(page, size)
        val posts = postRepository.findAllByOrderByIdDesc(pageable)

        // 전체 페이지 수를 보여주기 위해 페이지 요청마다 COUNT 쿼리를 실행한다.
        val totalElements = postRepository.count()
        val totalPages = if (totalElements == 0L) {
            0
        } else {
            ((totalElements - 1) / size + 1).toInt()
        }

        return PostPageResponse(
            posts = posts.map(PostResponse::from),
            page = page,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages,
            hasPrevious = page > 0,
            hasNext = page + 1 < totalPages,
        )
    }
}

data class PostPageResponse(
    val posts: List<PostResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
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
