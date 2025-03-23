package com.example.s1shipleeopcprivatelimited.api

import com.example.s1shipleeopcprivatelimited.model.Courier
import com.example.s1shipleeopcprivatelimited.model.ShippingRate
import com.example.s1shipleeopcprivatelimited.model.ShipmentRequest

class ShippingRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getAvailableCouriers(): List<Courier> {
        return apiService.getAvailableCouriers()
    }

    suspend fun calculateShippingRates(
        pickupPincode: String,
        deliveryPincode: String,
        weight: Double,
        dimensions: String
    ): List<ShippingRate> {
        val requestMap = mapOf(
            "pickupPincode" to pickupPincode,
            "deliveryPincode" to deliveryPincode,
            "weight" to weight,
            "dimensions" to dimensions
        )
        return apiService.calculateShippingRates(requestMap)
    }

    suspend fun bookShipment(shipmentRequest: ShipmentRequest): ShipmentResponse {
        return apiService.bookShipment(shipmentRequest)
    }
}