package com.mepapp.backend.entity

import jakarta.persistence.*
import java.util.*

enum class Role {
    ADMIN, STAFF
}

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    val phone: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role,

    @Column(nullable = false)
    val passwordHash: String,

    @Column(nullable = false)
    val status: String = "ACTIVE"
)
