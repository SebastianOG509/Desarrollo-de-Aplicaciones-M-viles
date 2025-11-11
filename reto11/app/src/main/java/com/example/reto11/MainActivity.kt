package com.example.reto11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reto11.network.*
// Eliminamos imports de retrofit2.Call, retrofit2.Callback, retrofit2.Response
import kotlinx.coroutines.launch // Importar para Coroutines

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    var prompt by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("Hola 👋 soy tu asistente con IA.") }
    var loading by remember { mutableStateOf(false) }

    // Necesitamos un CoroutineScope para llamar a la función suspend
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Chat con IA") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = response,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Escribe tu mensaje") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (prompt.isNotEmpty() && !loading) {
                        loading = true
                        response = "Pensando..."
                        // Lanzamos una Coroutine para la llamada a la API
                        coroutineScope.launch {
                            val reply = sendPrompt(prompt)
                            response = reply
                            loading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text(if (loading) "Esperando..." else "Enviar")
            }
        }
    }
}

// Cambiamos a una función 'suspend' que devuelve directamente un String
suspend fun sendPrompt(prompt: String): String {
    val request = GeminiRequest(
        contents = listOf(
            GeminiContent(parts = listOf(GeminiPart(text = prompt)))
        )
    )

    // La llamada es 'síncrona' dentro de la coroutine
    return try {
        val response = ApiClient.instance.generateContent(ApiClient.API_KEY, request)

        if (response.isSuccessful) {
            response.body()
                ?.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text ?: "Sin respuesta (pero la llamada fue exitosa)."
        } else {
            "Error API: ${response.code()}. Cuerpo del error: ${response.errorBody()?.string()}"
        }
    } catch (t: Throwable) {
        "Error de red o desconocido: ${t.message}"
    }
}