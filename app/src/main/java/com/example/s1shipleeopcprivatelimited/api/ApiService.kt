package com.example.s1shipleeopcprivatelimited.api

import com.example.s1shipleeopcprivatelimited.model.Courier
import com.example.s1shipleeopcprivatelimited.model.ShippingRate
import com.example.s1shipleeopcprivatelimited.model.ShipmentRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("couriers")
    suspend fun getAvailableCouriers(): List<Courier>

    @POST("calculate-rates")
    suspend fun calculateShippingRates(
        @Body request: Map<String, Any>
    ): List<ShippingRate>

    @POST("book-shipment")
    suspend fun bookShipment(@Body shipmentRequest: ShipmentRequest): ShipmentResponse
}

data class ShipmentResponse(
    val shipmentId: String,
    val trackingNumber: String,
    val status: String,
    val estimatedDelivery: String
)