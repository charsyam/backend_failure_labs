package com.charsyam.dbedu.lab

import com.charsyam.dbedu.domain.UserEntity
import com.charsyam.dbedu.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class Lab1LostUpdateService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun reset(): UserStateResponse {
        userRepository.deleteAllInBatch()
        val user = userRepository.save(UserEntity(email = "lost-update@example.com", likes = 0, comments = 0))
        return user.toResponse()
    }

    @Transactional(readOnly = true)
    fun get(id: Long): UserStateResponse = userRepository.findById(id).orElseThrow().toResponse()

    @Transactional
    fun unsafeIncreaseLikes(id: Long, delta: Int): UserStateResponse {
        val user = userRepository.findById(id).orElseThrow()
        user.likes += delta
        return user.toResponse()
    }

    @Transactional
    fun unsafeIncreaseComments(id: Long, delta: Int): UserStateResponse {
        val user = userRepository.findById(id).orElseThrow()
        user.comments += delta
        return user.toResponse()
    }

    private fun UserEntity.toResponse() = UserStateResponse(
        id = requireNotNull(id),
        email = email,
        likes = likes,
        comments = comments,
    )
}
