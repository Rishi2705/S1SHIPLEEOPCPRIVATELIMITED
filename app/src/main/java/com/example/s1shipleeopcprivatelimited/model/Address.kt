package com.example.s1shipleeopcprivatelimited.model

data class Address(
    val fullName: String,
    val phoneNumber: String,
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val state: String,
    val pincode: String,
    val country: String
)