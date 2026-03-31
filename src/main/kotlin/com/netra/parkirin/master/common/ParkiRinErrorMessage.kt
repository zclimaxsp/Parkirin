package com.netra.parkirin.master.common

enum class ParkiRinErrorMessage(val code: String, val message: String) {
    ZONE_NOT_FOUND("ZoneNotFound", "Tariff zone tidak ditemukan!"),
    TARIFF_RULE_NOT_FOUND("TariffRuleNotFound", "Tariff rule tidak ditemukan!"),
    STREET_NOT_FOUND("StreetNotFound", "Street tidak ditemukan!"),
    OFFICER_NOT_FOUND("OfficerNotFound", "Officer tidak ditemukan!"),
    VEHICLE_NOT_FOUND("VehicleNotFound", "Kendaraan tidak ditemukan!"),
    VEHICLE_PLATE_EXISTS("VehiclePlateExists", "Nomor plat kendaraan sudah terdaftar!"),
    SUBSCRIPTION_PLAN_NOT_FOUND("SubscriptionPlanNotFound", "Paket langganan tidak ditemukan!"),
    SUBSCRIPTION_NOT_FOUND("SubscriptionNotFound", "Langganan tidak ditemukan!"),
    ZONE_NOT_DETECTED("ZoneNotDetected", "Koordinat tidak berada dalam zona parkir manapun!"),
}
