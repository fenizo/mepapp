package com.mepapp.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "invoices")
data class Invoice(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    val job: Job,

    @Column(nullable = false, unique = true)
    val invoiceNumber: String,

    @Column(nullable = false)
    val subtotal: BigDecimal,

    @Column(nullable = false)
    val discountAmount: BigDecimal,

    @Column(nullable = false)
    val finalAmount: BigDecimal,

    val pdfUrl: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
