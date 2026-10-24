package com.example.groupe7app.ui.screens.comun

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// TEMPORAL: ocupa el lugar de las pantallas que aún no se programan
@Composable
fun EnConstruccion(titulo: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(titulo, style = MaterialTheme.typography.titleLarge)
        Text("En construcción", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}