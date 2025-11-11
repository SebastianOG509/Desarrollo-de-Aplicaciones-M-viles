package com.example.reto11.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // URL base correcta
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/"
    const val API_KEY = "AIzaSyCeiaXrVnIHKuEjuNMytMNvX3s0v1ZL630" // <-- tu clave de aistudio.google.com

    private val client = OkHttpClient.Builder().build()

    val instance: GeminiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiService::class.java)
    }
}
