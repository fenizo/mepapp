package com.mepapp.backend.util

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfWriter
import com.mepapp.backend.entity.Invoice
import java.io.ByteArrayOutputStream

object PdfGenerator {
    fun generateInvoicePdf(invoice: Invoice): ByteArray {
        val document = Document(PageSize.A4)
        val out = ByteArrayOutputStream()
        PdfWriter.getInstance(document, out)
        
        document.open()
        
        val fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f)
        val title = Paragraph("INVOICE", fontTitle)
        title.alignment = Element.ALIGN_CENTER
        document.add(title)
        
        document.add(Paragraph("\n"))
        document.add(Paragraph("Invoice Number: ${invoice.invoiceNumber}"))
        document.add(Paragraph("Date: ${invoice.createdAt}"))
        document.add(Paragraph("Customer: ${invoice.job.customer.name}"))
        document.add(Paragraph("Service: ${invoice.job.serviceType}"))
        
        document.add(Paragraph("\nItems:"))
        invoice.job.items.forEach {
            document.add(Paragraph("- ${it.description}: ${it.quantity} x ${it.price} = ${it.price.multiply(BigDecimal(it.quantity))}"))
        }
        
        document.add(Paragraph("\n"))
        document.add(Paragraph("Subtotal: ${invoice.subtotal}"))
        document.add(Paragraph("Discount (10%): ${invoice.discountAmount}"))
        document.add(Paragraph("Total Amount: ${invoice.finalAmount}"))
        
        document.close()
        return out.toByteArray()
    }
}
