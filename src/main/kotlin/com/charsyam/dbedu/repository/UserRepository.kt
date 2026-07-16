package com.charsyam.dbedu.repository

import com.charsyam.dbedu.domain.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long>
