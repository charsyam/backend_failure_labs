package com.charsyam.lab5

import com.charsyam.lab5.order.Order
import com.charsyam.lab5.order.OrderRepository
import com.charsyam.lab5.order.OrderStatus
import com.charsyam.lab5.payment.PaymentHistoryRepository
import com.charsyam.lab5.wallet.Wallet
import com.charsyam.lab5.wallet.WalletRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class Lab5Service(
    private val walletRepository: WalletRepository,
    private val orderRepository: OrderRepository,
    private val historyRepository: PaymentHistoryRepository,
) {
    @Transactional
    fun setup(orderCount: Int, orderAmount: Long, walletBalance: Long): SetupResponse {
        require(orderCount > 0)
        require(orderAmount > 0)
        require(walletBalance >= orderCount * orderAmount * 2) {
            "walletBalance must cover duplicate payment for this lab"
        }

        historyRepository.deleteAllInBatch()
        orderRepository.deleteAllInBatch()
        walletRepository.deleteAllInBatch()

        val wallet = walletRepository.save(Wallet("lab-user", walletBalance))
        val walletId = requireNotNull(wallet.id)
        orderRepository.saveAll((1..orderCount).map { Order(walletId, orderAmount) })
        return SetupResponse(walletId, orderCount, orderAmount, walletBalance)
    }

    @Transactional(readOnly = true)
    fun result(walletId: Long): LabResult {
        val wallet = walletRepository.findById(walletId).orElseThrow()
        val duplicated = historyRepository.findDuplicatedOrders(walletId).associate {
            (it[0] as Number).toLong() to (it[1] as Number).toLong()
        }
        return LabResult(
            walletId = walletId,
            remainingBalance = wallet.balance,
            beforePaymentOrders = orderRepository.countByWalletIdAndStatus(walletId, OrderStatus.BEFORE_PAYMENT),
            completedOrders = orderRepository.countByWalletIdAndStatus(walletId, OrderStatus.PAYMENT_COMPLETED),
            paymentHistoryCount = historyRepository.countByWalletId(walletId),
            duplicatedOrderHistories = duplicated,
        )
    }
}

data class SetupResponse(val walletId: Long, val orderCount: Int, val orderAmount: Long, val walletBalance: Long)
data class LabResult(
    val walletId: Long,
    val remainingBalance: Long,
    val beforePaymentOrders: Long,
    val completedOrders: Long,
    val paymentHistoryCount: Long,
    val duplicatedOrderHistories: Map<Long, Long>,
)
