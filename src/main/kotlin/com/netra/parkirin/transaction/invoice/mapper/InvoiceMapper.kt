package com.netra.parkirin.transaction.invoice.mapper

import com.netra.parkirin.transaction.invoice.dto.InvoiceDto
import com.netra.parkirin.transaction.invoice.model.Invoice

object InvoiceMapper {
    fun toDto(invoice: Invoice): InvoiceDto {
        return InvoiceDto(
            id = invoice.key.id,
            zoneId = invoice.key.zoneId,
            invoiceDate = invoice.key.invoiceDate,
            invoiceNumber = invoice.invoiceNumber,
            sessionId = invoice.sessionId,
            plateNumber = invoice.plateNumber,
            amount = invoice.amount,
            durationMinutes = invoice.durationMinutes,
            status = invoice.status,
            expiredAt = invoice.expiredAt,
            qrToken = invoice.qrToken,
            createdAt = invoice.createdAt,
            updatedAt = invoice.updatedAt,
        )
    }
}
