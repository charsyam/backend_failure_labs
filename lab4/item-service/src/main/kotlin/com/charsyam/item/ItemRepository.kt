package com.charsyam.item

import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository : JpaRepository<Item, Long>
