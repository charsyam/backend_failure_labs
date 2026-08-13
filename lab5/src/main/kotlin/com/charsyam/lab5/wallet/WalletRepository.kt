package com.charsyam.lab5.wallet

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface WalletRepository : JpaRepository<Wallet, Long> {
    @Query("select w.balance from Wallet w where w.id = :walletId")
    fun findBalance(@Param("walletId") walletId: Long): Long?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.id = :walletId")
    fun findByIdForUpdate(@Param("walletId") walletId: Long): Wallet?
}
