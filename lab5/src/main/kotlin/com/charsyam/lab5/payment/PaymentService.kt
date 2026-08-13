package com.charsyam.lab5.payment

import com.charsyam.lab5.order.OrderRepository
import com.charsyam.lab5.order.OrderStatus
import com.charsyam.lab5.wallet.WalletRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val walletRepository: WalletRepository,
    private val orderRepository: OrderRepository,
    private val historyRepository: PaymentHistoryRepository,
    private val entityManager: EntityManager,
) {
    @Transactional
    fun payAll(walletId: Long): PaymentBatchResponse {
        val balanceSeenBeforeLock = walletRepository.findBalance(walletId)
            ?: throw NoSuchElementException("Wallet $walletId does not exist")

        entityManager.clear()

        val lockedWallet = walletRepository.findByIdForUpdate(walletId)
            ?: throw NoSuchElementException("Wallet $walletId does not exist")
        val balanceAfterLock = lockedWallet.balance

        val orders = orderRepository.findAllByWalletIdAndStatusOrderById(walletId, OrderStatus.BEFORE_PAYMENT)
        val totalAmount = orders.sumOf { it.amount }

        lockedWallet.pay(totalAmount)
        orders.forEach {
            it.startPayment()
            it.completePayment()
        }
        historyRepository.saveAll(
            orders.map {
                PaymentHistory(
                    orderId = requireNotNull(it.id),
                    walletId = walletId,
                    amount = it.amount,
                )
            },
        )

        return PaymentBatchResponse(
            walletId = walletId,
            balanceSeenBeforeLock = balanceSeenBeforeLock,
            balanceAfterLock = balanceAfterLock,
            paidOrderIds = orders.map { requireNotNull(it.id) },
            paidAmount = totalAmount,
            remainingBalance = lockedWallet.balance,
        )
    }
}

data class PaymentBatchResponse(
    val walletId: Long,
    val balanceSeenBeforeLock: Long,
    val balanceAfterLock: Long,
    val paidOrderIds: List<Long>,
    val paidAmount: Long,
    val remainingBalance: Long,
)
