package com.example.reto7

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*

class MainActivity : ComponentActivity() {
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseDatabase.getInstance().getReference("games")

        setContent {
            GameListScreen(db) { gameId, isCreator ->
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("gameId", gameId)
                intent.putExtra("isCreator", isCreator)
                startActivity(intent)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(db: DatabaseReference, onJoinGame: (String, Boolean) -> Unit) {
    var games by remember { mutableStateOf(listOf<String>()) }
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Leer juegos disponibles
    LaunchedEffect(db) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()
                for (game in snapshot.children) {
                    val opponent = game.child("opponent").getValue(String::class.java)
                    if (opponent.isNullOrEmpty()) list.add(game.key ?: continue)
                }
                games = list
            }

            override fun onCancelled(error: DatabaseError) {
                errorMsg = "Error al leer partidas: ${error.message}"
            }
        }
        db.addValueEventListener(listener)

        // Remove listener when composable leaves composition
        onDispose { db.removeEventListener(listener) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Triqui Online") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (errorMsg != null) {
                Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    loading = true
                    val newId = db.push().key
                    if (newId != null) {
                        val newGame = mapOf(
                            "creator" to "JugadorA",
                            "opponent" to "",
                            "board" to ArrayList(List(9) { "" }),
                            "turn" to "JugadorA",
                            "winner" to ""
                        )
                        db.child(newId).setValue(newGame)
                            .addOnSuccessListener {
                                loading = false
                                onJoinGame(newId, true)
                            }
                            .addOnFailureListener {
                                loading = false
                                errorMsg = "Error al crear partida: ${it.message}"
                            }
                    } else {
                        loading = false
                        errorMsg = "Error al generar ID de partida"
                    }
                },
                enabled = !loading
            ) {
                Text(if (loading) "Creando..." else "Crear nueva partida")
            }

            Spacer(Modifier.height(20.dp))

            Text("Partidas disponibles:")

            if (games.isEmpty()) {
                Text("No hay partidas disponibles", modifier = Modifier.padding(12.dp))
            } else {
                LazyColumn {
                    items(games) { id ->
                        Text(
                            text = "Partida $id",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    loading = true
                                    db.child(id).child("opponent").setValue("JugadorB")
                                        .addOnSuccessListener {
                                            loading = false
                                            onJoinGame(id, false)
                                        }
                                        .addOnFailureListener {
                                            loading = false
                                            errorMsg = "Error al unirse: ${it.message}"
                                        }
                                }
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}
