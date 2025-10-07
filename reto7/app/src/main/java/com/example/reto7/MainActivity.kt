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

@Composable
fun GameListScreen(db: DatabaseReference, onJoinGame: (String, Boolean) -> Unit) {
    var games by remember { mutableStateOf(listOf<String>()) }

    // Leer juegos disponibles
    LaunchedEffect(true) {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()
                for (game in snapshot.children) {
                    val opponent = game.child("opponent").getValue(String::class.java)
                    if (opponent.isNullOrEmpty()) list.add(game.key!!)
                }
                games = list
            }
            override fun onCancelled(error: DatabaseError) {}
        })
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
            Button(onClick = {
                val newId = db.push().key!!
                db.child(newId).setValue(
                    mapOf(
                        "creator" to "JugadorA",
                        "opponent" to "",
                        "board" to List(9) { "" },
                        "turn" to "JugadorA",
                        "winner" to ""
                    )
                )
                onJoinGame(newId, true)
            }) {
                Text("Crear nueva partida")
            }

            Spacer(Modifier.height(20.dp))

            Text("Partidas disponibles:")
            LazyColumn {
                items(games) { id ->
                    Text(
                        text = "Partida $id",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                db.child(id).child("opponent").setValue("JugadorB")
                                onJoinGame(id, false)
                            }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}
