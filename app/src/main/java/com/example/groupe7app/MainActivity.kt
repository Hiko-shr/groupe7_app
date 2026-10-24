package com.example.groupe7app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.groupe7app.model.database.AppDatabase
import com.example.groupe7app.model.database.DataSeeder
import com.example.groupe7app.ui.navigation.AppNavigation
import com.example.groupe7app.ui.theme.Groupe7AppTheme
import com.example.groupe7app.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val factory by lazy { ViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            DataSeeder(AppDatabase.getInstance(this@MainActivity)).poblarSiEstaVacia()
        }

        setContent {
            Groupe7AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    AppNavigation(factory)
                }
            }
        }
    }
}