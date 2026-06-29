package com.netra.parkirin.officer.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatUtcToLocalTime(utcString: String?): String {
    if (utcString.isNullOrBlank()) return "--:--"
    return try {
        // Parse UTC Zulu String dari backend
        val instant = Instant.parse(utcString)

        // Sengat otomatis pake zona waktu HP petugas (WIB/WITA/WIT)
        val localDateTime = instant.atZone(ZoneId.systemDefault())

        // Format biar cuma muncul jam & menit (Contoh: "12:21")
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        localDateTime.format(formatter)
    } catch (e: Exception) {
        "--:--"
    }
}