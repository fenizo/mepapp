package com.mepapp.backend.service

import com.mepapp.backend.entity.Invoice
import com.mepapp.backend.entity.Job
import com.mepapp.backend.repository.InvoiceRepository
import com.mepapp.backend.repository.JobRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class InvoiceService(
    private val invoiceRepository: InvoiceRepository,
    private val jobRepository: JobRepository
) {

    @Transactional
    fun generateInvoice(jobId: UUID, materialCharge: BigDecimal, serviceCharge: BigDecimal): Invoice {
        val job = jobRepository.findById(jobId).orElseThrow { Exception("Job not found") }
        
        // Add material and service charges as items if they don't exist
        val materialItem = JobItem(job = job, description = "Material Charges", quantity = 1, price = materialCharge)
        val serviceItem = JobItem(job = job, description = "Service Charges", quantity = 1, price = serviceCharge)
        
        job.items.add(materialItem)
        job.items.add(serviceItem)
        jobRepository.save(job)
        
        val subtotal = job.items.sumOf { it.price.multiply(BigDecimal(it.quantity)) }
        val discount = if (job.qrDiscountApplied) subtotal.multiply(BigDecimal("0.10")) else BigDecimal.ZERO
        val finalAmount = subtotal.subtract(discount)
        
        val invoiceNumber = "INV-${LocalDateTime.now().year}-${(1000..9999).random()}"
        
        val invoice = Invoice(
            job = job,
            invoiceNumber = invoiceNumber,
            subtotal = subtotal,
            discountAmount = discount,
            finalAmount = finalAmount
        )
        
        return invoiceRepository.save(invoice)
    }

    fun getInvoice(id: UUID): Invoice = invoiceRepository.findById(id).orElseThrow { Exception("Invoice not found") }
}
