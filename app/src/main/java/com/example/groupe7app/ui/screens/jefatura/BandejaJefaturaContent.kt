package com.example.groupe7app.ui.screens.jefatura

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.groupe7app.ui.components.EstadoVacio
import com.example.groupe7app.ui.components.formatoClp
import com.example.groupe7app.ui.components.formatoFecha
import com.example.groupe7app.viewmodel.BandejaJefaturaUiState
import com.example.groupe7app.viewmodel.RendicionPendiente

// Bandeja de la jefatura: rendiciones de su equipo esperando revisión (la más antigua primero)
@Composable
fun BandejaJefaturaContent(
    estado: BandejaJefaturaUiState,
    onAbrirRendicion: (Long) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(24.dp))
            Text("Bandeja de aprobación", style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(
                text = when (estado.pendientes.size) {
                    0 -> "No tienes rendiciones pendientes"
                    1 -> "1 rendición esperando tu revisión"
                    else -> "${estado.pendientes.size} rendiciones esperando tu revisión"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.height(16.dp))

            when {
                estado.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                estado.pendientes.isEmpty() -> EstadoVacio(mensaje = "Todo al día: tu equipo no tiene rendiciones por revisar.")
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(estado.pendientes, key = { it.rendicion.id }) { pendiente ->
                        TarjetaPendiente(pendiente, onClick = { onAbrirRendicion(pendiente.rendicion.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaPendiente(p: RendicionPendiente, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(p.rendicion.numero, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline)
                Text(
                    if (p.diasEspera == 0) "Enviada hoy" else "Esperando ${p.diasEspera} día(s)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(p.rendicion.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(p.tecnico, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${formatoFecha(p.rendicion.desde)} al ${formatoFecha(p.rendicion.hasta)} · ${p.cantidadGastos} gastos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(formatoClp(p.total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            // Aviso si algún gasto supera el tope de su categoría
            if (p.alertas > 0) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null,
                        tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${p.alertas} gasto(s) sobre el tope", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
