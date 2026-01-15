package com.mepapp.backend.repository

import com.mepapp.backend.entity.Invoice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface InvoiceRepository : JpaRepository<Invoice, UUID> {
    fun findByJobId(jobId: UUID): Invoice?
    fun findByInvoiceNumber(invoiceNumber: String): Invoice?
}
