package com.charsyam.lab5.payment

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "lab5_payment_histories",
    indexes = [Index(name = "idx_lab5_history_order_id", columnList = "order_id")],
)
class PaymentHistory(
    @Column(name = "order_id", nullable = false)
    val orderId: Long,

    @Column(name = "wallet_id", nullable = false)
    val walletId: Long,

    @Column(nullable = false)
    val amount: Long,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set
}
