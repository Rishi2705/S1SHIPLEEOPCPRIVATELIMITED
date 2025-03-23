package com.example.s1shipleeopcprivatelimited.model

data class ShipmentRequest(
    val pickupAddress: Address,
    val deliveryAddress: Address,
    val packageWeight: Double,
    val packageDimensions: String,
    val selectedCourierId: String,
    val isFragile: Boolean,
    val notes: String
)
