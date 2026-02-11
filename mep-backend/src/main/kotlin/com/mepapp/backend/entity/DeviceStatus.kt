package com.mepapp.backend.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "device_status")
data class DeviceStatus(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    val userId: UUID,

    @Column(nullable = false)
    val userName: String,

    @Column(nullable = false)
    val lastSeen: Instant = Instant.now(),

    @Column(nullable = false)
    val networkType: String = "unknown",

    @Column(nullable = false)
    val appVersion: String = "unknown"
)
