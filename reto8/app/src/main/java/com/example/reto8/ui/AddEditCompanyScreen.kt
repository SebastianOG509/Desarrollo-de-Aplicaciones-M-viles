package com.example.reto8.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.reto8.data.Company
import com.example.reto8.data.CompanyDao

@Composable
fun AddEditCompanyScreen(
    dao: CompanyDao,
    onRefresh: () -> Unit,
    existing: Company? = null,
    onDone: () -> Unit
) {
    // 🔹 Campos de texto con valores iniciales (si se está editando)
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var classification by remember { mutableStateOf(existing?.classification ?: "Consultoría") }
    var url by remember { mutableStateOf(existing?.url ?: "") }
    var phone by remember { mutableStateOf(existing?.phone ?: "") }
    var email by remember { mutableStateOf(existing?.email ?: "") }
    var products by remember { mutableStateOf(existing?.products ?: "") }

    var expanded by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🏷️ Título
        Text(
            text = if (existing == null) "Agregar Empresa" else "Editar Empresa",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        // 🏢 Nombre
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; showError = false },
            label = { Text("Nombre de la empresa") },
            modifier = Modifier.fillMaxWidth(),
            isError = showError
        )

        if (showError) {
            Text(
                text = "El nombre no puede estar vacío",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(12.dp))

        // 🏷️ Clasificación
        Box {
            Button(onClick = { expanded = true }) {
                Text(classification)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("Consultoría", "Desarrollo a la medida", "Fábrica").forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            classification = it
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // 🌐 URL
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Sitio web (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // 📞 Teléfono
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Teléfono (opcional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // 📧 Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico (opcional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // 🛍️ Productos
        OutlinedTextField(
            value = products,
            onValueChange = { products = it },
            label = { Text("Productos / Servicios (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        // 🧭 Botones de acción
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        showError = true
                    } else {
                        val company = existing?.copy(
                            name = name,
                            classification = classification,
                            url = url,
                            phone = phone,
                            email = email,
                            products = products
                        ) ?: Company(
                            name = name,
                            classification = classification,
                            url = url,
                            phone = phone,
                            email = email,
                            products = products
                        )

                        dao.insertOrUpdate(company)
                        onRefresh()
                        onDone() // 👈 Regresa a la lista
                    }
                }
            ) {
                Text(if (existing == null) "Guardar" else "Actualizar")
            }

            OutlinedButton(onClick = { onDone() }) {
                Text("Cancelar")
            }
        }
    }
}
