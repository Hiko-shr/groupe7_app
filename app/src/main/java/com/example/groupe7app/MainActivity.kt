package com.example.groupe7app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groupe7app.model.database.AppDatabase
import com.example.groupe7app.model.database.DataSeeder
import com.example.groupe7app.model.enums.Rol
import com.example.groupe7app.ui.screens.login.LoginScreen
import com.example.groupe7app.ui.screens.tecnico.TecInicioScreen
import com.example.groupe7app.ui.theme.Groupe7AppTheme
import com.example.groupe7app.viewmodel.LoginViewModel
import com.example.groupe7app.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

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
                Scaffold { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        // TEMPORAL: en el paso 8b esto lo reemplaza la navegación
                        val vm: LoginViewModel = viewModel(factory = ViewModelFactory(application))
                        var rol by remember { mutableStateOf<Rol?>(null) }
                        if (rol == null) {
                            LoginScreen(vm, onIngresoExitoso = { rol = it })
                        } else {
                            Text("Ingresaste como $rol")
                        }
                    }
                }
            }
        }
    }
}