package com.example.reto8.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.reto8.data.CompanyDao
import com.example.reto8.data.Company
import com.example.reto8.ui.theme.Reto8Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = CompanyDao(this)
        setContent {
            Reto8Theme {
                Surface {
                    var refresh by remember { mutableStateOf(true) }
                    if (refresh) {
                        CompanyListScreen(
                            dao = dao,
                            onRefresh = { refresh = !refresh }
                        )
                    }
                }
            }
        }
    }
}
