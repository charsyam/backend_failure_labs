package com.charsyam.lab5.wallet

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "lab5_wallets")
class Wallet(
    @Column(nullable = false, length = 100)
    val owner: String,

    @Column(nullable = false)
    var balance: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    fun pay(amount: Long) {
        check(balance >= amount) { "Insufficient wallet balance" }
        balance -= amount
    }
}
