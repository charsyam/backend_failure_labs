package com.charsyam.dbedu.lab

import com.charsyam.dbedu.domain.CommentEntity
import com.charsyam.dbedu.repository.CommentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.system.measureTimeMillis

@Service
class Lab2BatchInsertService(
    private val commentRepository: CommentRepository,
) {
    @Transactional
    fun clear(): Long {
        val count = commentRepository.count()
        commentRepository.deleteAllInBatch()
        return count
    }

    @Transactional
    fun saveLoop(rows: Int): BatchResponse {
        val elapsed = measureTimeMillis {
            (1..rows).forEach { i ->
                commentRepository.save(CommentEntity(userId = 1, content = "save-loop-$i"))
            }
        }
        return BatchResponse("save()", rows, elapsed)
    }
}
