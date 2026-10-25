package com.example.groupe7app.ui.screens.perfil

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groupe7app.model.entity.Usuario
import com.example.groupe7app.viewmodel.SesionViewModel

@Composable
fun PerfilScreen(usuario: Usuario, sesionVm: SesionViewModel, onCerrarSesion: () -> Unit) {
    // null = aún no eligió: se muestra según el tema del teléfono
    val temaGuardado by sesionVm.temaOscuro.collectAsStateWithLifecycle()
    val oscuro = temaGuardado ?: isSystemInDarkTheme()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Perfil", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(usuario.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(usuario.correo, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Rol: " + usuario.rol.name.lowercase().replaceFirstChar { it.uppercase() })
            }
        }

        // Tema oscuro: se guarda en DataStore y se aplica a toda la app al instante
        Card(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.DarkMode, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("Tema oscuro", modifier = Modifier.weight(1f))
                Switch(checked = oscuro, onCheckedChange = sesionVm::cambiarTema)
            }
        }

        Spacer(Modifier.weight(1f))   // empuja el botón hacia abajo

        OutlinedButton(
            onClick = onCerrarSesion,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
    }
}
