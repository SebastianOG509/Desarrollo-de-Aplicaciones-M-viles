package com.example.reto11.network

import retrofit2.Response // Cambiamos de Call a Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

// Las Data Classes están bien, pero eliminaremos la duplicidad de retrofit2.Response/Call
data class GeminiRequest(val contents: List<GeminiContent>)
data class GeminiContent(val parts: List<GeminiPart>)
data class GeminiPart(val text: String)
data class GeminiResponse(val candidates: List<GeminiCandidate>)
data class GeminiCandidate(val content: GeminiContent)

interface GeminiService {
    @Headers("Content-Type: application/json")
    @POST("models/gemini-2.5-flash:generateContent") // ¡Recomendación! Usar un modelo disponible
    // NOTA: Cambiamos de gemini-1.5-flash-latest al modelo estable recomendado gemini-2.5-flash

    // 1. Añadimos 'suspend'
    // 2. Cambiamos el tipo de retorno a Response<GeminiResponse> (de Retrofit 2)
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}