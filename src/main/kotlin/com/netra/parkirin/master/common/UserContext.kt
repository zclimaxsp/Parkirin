package com.netra.parkirin.master.common

import org.springframework.web.server.ServerWebExchange

fun ServerWebExchange.getUserId(): String? {
    return request.headers.getFirst("X-User-Id")
}
