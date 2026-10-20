package com.example.groupe7app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.groupe7app.ui.theme.Groupe7AppTheme
import com.example.groupe7app.ui.screens.tecnico.TecInicioScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Groupe7AppTheme {
                // Llamamos directamente a la pantalla que acabamos de crear
                TecInicioScreen()
            }
        }
    }
}