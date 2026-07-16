package com.charsyam.dbedu.repository

import com.charsyam.dbedu.domain.CommentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<CommentEntity, Long>
