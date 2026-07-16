package com.charsyam.dbedu.lab

import java.math.BigDecimal
import java.time.LocalDateTime

data class IdResponse(val id: Long)

data class UserStateResponse(
    val id: Long,
    val email: String,
    val likes: Int,
    val comments: Int,
)

data class BatchResponse(
    val method: String,
    val rows: Int,
    val elapsedMs: Long,
)

data class CountResponse(
    val label: String,
    val count: Long,
    val elapsedMs: Long,
    val explain: List<Map<String, Any?>>,
)

data class VisibilityResponse(
    val orderId: Long,
    val visibleToExternalReadBeforeCommit: Boolean,
    val note: String,
)

data class CouponResponse(
    val issued: Boolean,
    val couponId: Long?,
    val currentIssuedCount: Long,
)

data class OrderRowResponse(
    val id: Long,
    val userId: Long,
    val status: String,
    val amount: BigDecimal,
    val createdAt: LocalDateTime,
)
