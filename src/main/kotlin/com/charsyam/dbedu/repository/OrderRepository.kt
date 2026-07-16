package com.charsyam.dbedu.repository

import com.charsyam.dbedu.domain.OrderEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<OrderEntity, Long> {
    fun findByStatus(status: String, pageable: Pageable): List<OrderEntity>

    fun findByUserIdOrderByCreatedAtDescIdDesc(userId: Long, pageable: Pageable): List<OrderEntity>
}
