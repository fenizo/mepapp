package com.mepapp.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "job_items")
data class JobItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    val job: Job,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false)
    val quantity: Int,

    @Column(nullable = false)
    val price: BigDecimal
)
