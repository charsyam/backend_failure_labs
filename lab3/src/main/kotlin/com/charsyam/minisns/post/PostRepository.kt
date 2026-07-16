package com.charsyam.minisns.post

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long> {
    fun findAllByOrderByIdDesc(pageable: Pageable): List<Post>
}
