package com.charsyam.lab5.payment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PaymentHistoryRepository : JpaRepository<PaymentHistory, Long> {
    fun countByWalletId(walletId: Long): Long

    @Query("select h.orderId, count(h) from PaymentHistory h where h.walletId = :walletId group by h.orderId having count(h) > 1")
    fun findDuplicatedOrders(walletId: Long): List<Array<Any>>
}
