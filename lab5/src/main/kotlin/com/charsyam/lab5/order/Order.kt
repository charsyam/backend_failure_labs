package com.charsyam.lab5.order

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "lab5_orders",
    indexes = [Index(name = "idx_lab5_orders_wallet_status", columnList = "wallet_id,status")],
)
class Order(
    @Column(name = "wallet_id", nullable = false)
    val walletId: Long,

    @Column(nullable = false)
    val amount: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: OrderStatus = OrderStatus.BEFORE_PAYMENT
        protected set

    fun startPayment() {
        check(status == OrderStatus.BEFORE_PAYMENT)
        status = OrderStatus.PAYMENT_IN_PROGRESS
    }

    fun completePayment() {
        check(status == OrderStatus.PAYMENT_IN_PROGRESS)
        status = OrderStatus.PAYMENT_COMPLETED
    }

    fun failPayment() {
        check(status == OrderStatus.PAYMENT_IN_PROGRESS)
        status = OrderStatus.PAYMENT_FAILED
    }
}

enum class OrderStatus {
    BEFORE_PAYMENT,
    PAYMENT_IN_PROGRESS,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
}
