package com.charsyam.dbedu.lab

import com.charsyam.dbedu.domain.CouponEntity
import com.charsyam.dbedu.repository.CouponRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class Lab6CouponService(
    private val couponRepository: CouponRepository,
) {
    @Transactional
    fun clear(): Long {
        val count = couponRepository.count()
        couponRepository.deleteAllInBatch()
        return count
    }

    @Transactional
    fun unsafeIssue(userId: Long, policyId: Long, sleepMs: Long): CouponResponse {
        if (couponRepository.existsByUserIdAndPolicyId(userId, policyId)) {
            return CouponResponse(false, null, couponRepository.countByUserIdAndPolicyId(userId, policyId))
        }
        Thread.sleep(sleepMs)
        val coupon = couponRepository.save(CouponEntity(userId, policyId, "BAD-${UUID.randomUUID()}"))
        return CouponResponse(true, requireNotNull(coupon.id), couponRepository.countByUserIdAndPolicyId(userId, policyId))
    }
}
