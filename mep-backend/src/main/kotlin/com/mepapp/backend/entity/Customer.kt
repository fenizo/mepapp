package com.mepapp.backend.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "customers")
data class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    val phone: String,

    @Column(nullable = false)
    val address: String
)
