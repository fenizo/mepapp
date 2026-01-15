package com.mepapp.backend.repository

import com.mepapp.backend.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByPhone(phone: String): User?
}
