package com.charsyam.minisns.post

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "posts")
class Post(
    @Column(nullable = false, length = 200)
    val title: String,

    @Column(nullable = false, columnDefinition = "text")
    val content: String,

    @Column(nullable = false)
    val likes: Long = 0,

    @Column(nullable = false)
    val comments: Long = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set
}
