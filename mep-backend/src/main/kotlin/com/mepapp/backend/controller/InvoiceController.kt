package com.mepapp.backend.controller

import com.mepapp.backend.service.InvoiceService
import com.mepapp.backend.util.PdfGenerator
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/invoices")
class InvoiceController(private val invoiceService: InvoiceService) {

    @PostMapping("/generate/{jobId}")
    fun generate(
        @PathVariable jobId: UUID,
        @RequestParam materialCharge: java.math.BigDecimal,
        @RequestParam serviceCharge: java.math.BigDecimal
    ) = invoiceService.generateInvoice(jobId, materialCharge, serviceCharge)

    @GetMapping("/{id}/pdf")
    fun downloadPdf(@PathVariable id: UUID): ResponseEntity<ByteArray> {
        val invoice = invoiceService.getInvoice(id)
        val pdf = PdfGenerator.generateInvoicePdf(invoice)
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_${invoice.invoiceNumber}.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf)
    }
}
