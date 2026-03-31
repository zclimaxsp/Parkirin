package com.netra.parkirin.transaction.common

import org.springframework.web.server.ServerWebExchange
import java.util.UUID

object UserContext {
    fun getUserId(exchange: ServerWebExchange): UUID? {
        val header = exchange.request.headers.getFirst("X-User-Id")
        return if (header != null) runCatching { UUID.fromString(header) }.getOrNull() else null
    }

    fun requireUserId(exchange: ServerWebExchange): UUID {
        return getUserId(exchange) ?: error("Missing X-User-Id header")
    }
}
