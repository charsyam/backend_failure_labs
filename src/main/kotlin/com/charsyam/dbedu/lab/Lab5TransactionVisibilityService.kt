package com.charsyam.dbedu.lab

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class Lab5TransactionVisibilityService(
    private val orderData: LabOrderDataService,
    private val partnerOrderQueue: PartnerOrderQueue,
) {
    @Transactional
    fun badCallExternalBeforeCommit(userId: Long, sleepBeforeCommitMs: Long): VisibilityResponse {
        val order = orderData.createOrder(userId)
        val orderId = requireNotNull(order.id)
        partnerOrderQueue.publish(PartnerOrderMessage(orderId = orderId))
        Thread.sleep(sleepBeforeCommitMs)
        return VisibilityResponse(
            orderId = orderId,
            visibleToExternalReadBeforeCommit = false,
            note = "주문 트랜잭션이 커밋되기 전에 상대 서비스 큐 메시지를 발행했다. 상대 서비스가 메시지를 빨리 소비하면 우리 주문 조회 API에서 404를 받을 수 있다.",
        )
    }
}
