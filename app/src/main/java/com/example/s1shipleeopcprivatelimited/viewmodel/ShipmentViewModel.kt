package com.example.s1shipleeopcprivatelimited.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.s1shipleeopcprivatelimited.api.ShipmentResponse
import com.example.s1shipleeopcprivatelimited.api.ShippingRepository
import com.example.s1shipleeopcprivatelimited.model.Address
import com.example.s1shipleeopcprivatelimited.model.Courier
import com.example.s1shipleeopcprivatelimited.model.ShipmentRequest
import com.example.s1shipleeopcprivatelimited.model.ShippingRate
import kotlinx.coroutines.launch

class ShipmentViewModel : ViewModel() {
    private val repository = ShippingRepository()

    // LiveData for UI updates
    private val _couriers = MutableLiveData<List<Courier>>()
    val couriers: LiveData<List<Courier>> = _couriers

    private val _shippingRates = MutableLiveData<List<ShippingRate>>()
    val shippingRates: LiveData<List<ShippingRate>> = _shippingRates

    private val _selectedCourier = MutableLiveData<Courier>()
    val selectedCourier: LiveData<Courier> = _selectedCourier

    private val _selectedRate = MutableLiveData<ShippingRate>()
    val selectedRate: LiveData<ShippingRate> = _selectedRate

    private val _shipmentResponse = MutableLiveData<ShipmentResponse>()
    val shipmentResponse: LiveData<ShipmentResponse> = _shipmentResponse

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Form data
    val pickupAddress = MutableLiveData<Address>()
    val deliveryAddress = MutableLiveData<Address>()
    val packageWeight = MutableLiveData<Double>()
    val packageDimensions = MutableLiveData<String>()
    val isFragile = MutableLiveData<Boolean>()
    val notes = MutableLiveData<String>()

    init {
        fetchCouriers()
    }

    private fun fetchCouriers() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val result = repository.getAvailableCouriers()
                _couriers.value = result
            } catch (e: Exception) {
                _error.value = "Failed to load couriers: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun calculateRates(pickupPincode: String, deliveryPincode: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val weight = packageWeight.value ?: 1.0
                val dimensions = packageDimensions.value ?: "30x30x30"

                val rates = repository.calculateShippingRates(
                    pickupPincode,
                    deliveryPincode,
                    weight,
                    dimensions
                )
                _shippingRates.value = rates
            } catch (e: Exception) {
                _error.value = "Failed to calculate rates: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun selectCourier(courier: Courier) {
        _selectedCourier.value = courier

        // Find corresponding rate
        _shippingRates.value?.find { it.courierId == courier.id }?.let {
            _selectedRate.value = it
        }
    }

    fun bookShipment() {
        viewModelScope.launch {
            try {
                _loading.value = true

                val request = ShipmentRequest(
                    pickupAddress = pickupAddress.value!!,
                    deliveryAddress = deliveryAddress.value!!,
                    packageWeight = packageWeight.value ?: 1.0,
                    packageDimensions = packageDimensions.value ?: "30x30x30",
                    selectedCourierId = selectedCourier.value?.id ?: "",
                    isFragile = isFragile.value ?: false,
                    notes = notes.value ?: ""
                )

                val response = repository.bookShipment(request)
                _shipmentResponse.value = response
            } catch (e: Exception) {
                _error.value = "Failed to book shipment: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}
