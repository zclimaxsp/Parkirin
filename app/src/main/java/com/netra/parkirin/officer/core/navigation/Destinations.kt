package com.netra.parkirin.officer.core.navigation

object Destinations {
    const val SPLASH = "splash"
    const val LOGIN = "login"

    // Main (bottom nav)
    const val DASHBOARD = "dashboard"
    const val SESSIONS = "sessions"
    const val SETTINGS = "settings"

    // Entry flow
    const val PARKING_ENTRY = "parking/entry"
    const val ENTRY_RESULT = "parking/entry/result"

    // Exit flow
    const val PARKING_EXIT = "parking/exit"
    const val EXIT_PAYMENT = "parking/exit/payment/{invoiceId}"
    const val EXIT_RESULT = "parking/exit/result"

    fun exitPayment(invoiceId: String) = "parking/exit/payment/$invoiceId"
}
