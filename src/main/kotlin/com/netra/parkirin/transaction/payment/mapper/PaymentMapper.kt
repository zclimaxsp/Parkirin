package com.netra.parkirin.transaction.payment.mapper

import com.netra.parkirin.transaction.payment.dto.PaymentDto
import com.netra.parkirin.transaction.payment.model.Payment

object PaymentMapper {
    fun toDto(payment: Payment): PaymentDto {
        return PaymentDto(
            id = payment.key.id,
            zoneId = payment.key.zoneId,
            paymentDate = payment.key.paymentDate,
            invoiceId = payment.invoiceId,
            method = payment.method,
            amount = payment.amount,
            receiptNumber = payment.receiptNumber,
            gatewayRef = null,
            paidAt = payment.paidAt,
            createdAt = payment.createdAt,
        )
    }
}