package com.example.reto8.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reto8.data.Company
import com.example.reto8.data.CompanyDao

@Composable
fun CompanyListScreen(dao: CompanyDao, onRefresh: () -> Unit) {
    var search by remember { mutableStateOf("") }
    var classification by remember { mutableStateOf("Todos") }
    var companies by remember { mutableStateOf(dao.getAll()) }
    var showDialog by remember { mutableStateOf(false) }
    var companyToDelete by remember { mutableStateOf<Company?>(null) }

    // 👇 Estados de control de pantalla
    var showForm by remember { mutableStateOf(false) }
    var companyToEdit by remember { mutableStateOf<Company?>(null) }

    fun reload() {
        companies = dao.filter(search, classification)
    }

    // ✅ Si estamos en modo formulario, mostramos el componente de agregar/editar
    if (showForm) {
        AddEditCompanyScreen(
            dao = dao,
            onRefresh = { reload() },
            existing = companyToEdit,
            onDone = {
                showForm = false // 👈 vuelve a la lista
                companyToEdit = null
                reload()
            }
        )
        return
    }

    // ✅ Pantalla principal con lista
    Column(Modifier.padding(16.dp)) {
        // 🔍 Barra de búsqueda + filtro
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it; reload() },
                label = { Text("Buscar") },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) {
                    Text(classification)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Todos", "Consultoría", "Desarrollo a la medida", "Fábrica").forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                classification = it
                                expanded = false
                                reload()
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ➕ Botón para agregar nueva empresa
        Button(onClick = {
            companyToEdit = null
            showForm = true
        }) {
            Text("Agregar Empresa")
        }

        Spacer(Modifier.height(16.dp))

        // 📋 Lista de empresas
        LazyColumn {
            items(companies) { company ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(company.name, style = MaterialTheme.typography.titleMedium)
                        Text(company.classification)

                        Row {
                            TextButton(onClick = {
                                companyToEdit = company
                                showForm = true
                            }) { Text("Editar") }

                            TextButton(onClick = {
                                companyToDelete = company
                                showDialog = true
                            }) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }

    // 🗑️ Diálogo de confirmación al eliminar
    if (showDialog && companyToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Eliminar ${companyToDelete!!.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    dao.delete(companyToDelete!!.id)
                    reload()
                    showDialog = false
                }) { Text("Sí") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("No") }
            }
        )
    }
}
