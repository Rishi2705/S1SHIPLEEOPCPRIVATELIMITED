package com.example.s1shipleeopcprivatelimited.model

data class ShippingRate(
    val courierId: String,
    val baseRate: Double,
    val distanceRate: Double,
    val weightRate: Double,
    val tax: Double,
    val totalAmount: Double,
    val currency: String = "INR",
    val estimatedDeliveryTime: String
)
