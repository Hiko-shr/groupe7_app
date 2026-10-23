package com.example.groupe7app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.groupe7app.model.database.AppDatabase
import com.example.groupe7app.model.database.DataSeeder
import com.example.groupe7app.ui.screens.tecnico.TecInicioScreen
import com.example.groupe7app.ui.theme.Groupe7AppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // TEMPORAL: cargar datos de prueba (en el paso 7 se mueve a la clase Application)
        lifecycleScope.launch {
            DataSeeder(AppDatabase.getInstance(this@MainActivity)).poblarSiEstaVacia()
        }

        setContent {
            Groupe7AppTheme {
                TecInicioScreen()
            }
        }
    }
}