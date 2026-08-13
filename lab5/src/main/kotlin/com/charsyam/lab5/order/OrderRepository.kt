package com.charsyam.lab5.order

import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
    fun findAllByWalletIdAndStatusOrderById(walletId: Long, status: OrderStatus): List<Order>
    fun countByWalletIdAndStatus(walletId: Long, status: OrderStatus): Long
}
