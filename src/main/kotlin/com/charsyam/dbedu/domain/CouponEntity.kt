package com.charsyam.dbedu.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "coupons")
class CouponEntity(
    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "policy_id", nullable = false)
    var policyId: Long,

    @Column(nullable = false, length = 80)
    var code: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
