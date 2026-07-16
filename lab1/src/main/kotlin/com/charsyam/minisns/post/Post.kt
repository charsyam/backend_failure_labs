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
    var title: String,

    @Column(nullable = false, columnDefinition = "text")
    var content: String,

    @Column(nullable = false)
    var likes: Long = 0,

    @Column(nullable = false)
    var comments: Long = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    fun increaseLikes() {
        likes += 1
    }

    fun increaseComments() {
        comments += 1
    }
}
