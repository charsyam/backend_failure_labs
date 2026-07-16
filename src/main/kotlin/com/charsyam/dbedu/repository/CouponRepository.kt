package com.charsyam.dbedu.repository

import com.charsyam.dbedu.domain.CouponEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CouponRepository : JpaRepository<CouponEntity, Long> {
    fun existsByUserIdAndPolicyId(userId: Long, policyId: Long): Boolean

    fun countByUserIdAndPolicyId(userId: Long, policyId: Long): Long
}
