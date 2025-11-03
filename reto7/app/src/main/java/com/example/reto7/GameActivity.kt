package com.example.reto7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.*


class GameActivity : ComponentActivity() {
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentIntent = intent
        val gameId = currentIntent.getStringExtra("gameId") ?: return
        val isCreator = currentIntent.getBooleanExtra("isCreator", true)
        val player = if (isCreator) "JugadorA" else "JugadorB"

        db = FirebaseDatabase.getInstance().getReference("games").child(gameId)

        setContent {
            OnlineTriqui(db, player)
        }
    }

}

@Composable
fun OnlineTriqui(db: DatabaseReference, player: String) {
    var board by remember { mutableStateOf(List(9) { "" }) }
    var turn by remember { mutableStateOf("JugadorA") }
    var winner by remember { mutableStateOf("") }

    // Escuchar cambios en tiempo real
    LaunchedEffect(true) {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newBoard = snapshot.child("board").children.map { it.getValue(String::class.java) ?: "" }
                val newTurn = snapshot.child("turn").getValue(String::class.java) ?: "JugadorA"
                val newWinner = snapshot.child("winner").getValue(String::class.java) ?: ""
                board = newBoard
                turn = newTurn
                winner = newWinner
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun checkWinner(board: List<String>): String {
        val combos = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        for (c in combos) {
            val (a, b, c3) = c
            if (board[a].isNotEmpty() && board[a] == board[b] && board[a] == board[c3]) {
                return board[a]
            }
        }
        return ""
    }

    fun makeMove(index: Int) {
        if (board[index].isEmpty() && winner.isEmpty() && turn == player) {
            val mark = if (player == "JugadorA") "X" else "O"
            val newBoard = board.toMutableList()
            newBoard[index] = mark
            db.child("board").setValue(newBoard)
            val newWinner = checkWinner(newBoard)
            if (newWinner.isNotEmpty()) {
                db.child("winner").setValue(player)
            } else if (newBoard.none { it.isEmpty() }) {
                db.child("winner").setValue("Empate")
            } else {
                db.child("turn").setValue(if (player == "JugadorA") "JugadorB" else "JugadorA")
            }
        }
    }

    // UI del tablero
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when {
                winner == "Empate" -> "Empate 🤝"
                winner.isNotEmpty() -> "Ganó: $winner"
                else -> if (turn == player) "Tu turno ($player)" else "Esperando..."
            },
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        for (row in 0..2) {
            Row {
                for (col in 0..2) {
                    val index = row * 3 + col
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.DarkGray)
                            .padding(4.dp)
                            .clickable { makeMove(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = board[index],
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
