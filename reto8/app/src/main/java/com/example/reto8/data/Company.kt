package com.example.reto8.data

data class Company(
    val id: Long = 0L,
    val name: String,
    val classification: String,
    val url: String = "",
    val phone: String = "",
    val email: String = "",
    val products: String = ""
)
