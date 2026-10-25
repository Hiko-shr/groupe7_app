package com.example.groupe7app.ui.screens.perfil

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun PerfilContent(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var temaOscuro by remember { mutableStateOf(false) }

    // 1. Estados reales de los permisos verificados en tiempo real
    var permisoCamaraConcedido by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var permisoUbicacionConcedido by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 2. Estados para los diálogos de aviso
    var mostrarAvisoCamara by remember { mutableStateOf(false) }
    var mostrarAvisoUbicacion by remember { mutableStateOf(false) }

    // 3. Lanzadores nativos
    val solicitarCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { concedido ->
            permisoCamaraConcedido = concedido
            if (!concedido) {
                Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val solicitarUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { concedido ->
            permisoUbicacionConcedido = concedido
            if (!concedido) {
                Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val colorPrimario = Color(0xFF0F4C5C)

    // --- DIÁLOGOS DE AVISO PREVIO (Se cierran al instante de confirmar) ---

    if (mostrarAvisoCamara) {
        AlertDialog(
            onDismissRequest = { mostrarAvisoCamara = false },
            title = { Text(text = "Permiso de Cámara", fontWeight = FontWeight.Bold) },
            text = { Text("TeqGastos necesita acceder a tu cámara para que puedas fotografiar las boletas y facturas físicas para adjuntarlas a tus rendiciones.") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarAvisoCamara = false // Cierra el aviso inmediatamente
                        solicitarCamara.launch(Manifest.permission.CAMERA) // Abre el de Android
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrimario)
                ) {
                    Text("Entendido y Continuar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarAvisoCamara = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    if (mostrarAvisoUbicacion) {
        AlertDialog(
            onDismissRequest = { mostrarAvisoUbicacion = false },
            title = { Text(text = "Permiso de Ubicación", fontWeight = FontWeight.Bold) },
            text = { Text("La aplicación necesita tu ubicación para registrar automáticamente el lugar exacto donde realizaste el gasto en terreno por temas de auditoría.") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarAvisoUbicacion = false // Cierra el aviso inmediatamente
                        solicitarUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION) // Abre el de Android
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrimario)
                ) {
                    Text("Entendido y Continuar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarAvisoUbicacion = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    // --- INTERFAZ DEL PERFIL ---
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Tarjeta Usuario
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(colorPrimario),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("MF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Column {
                        Text("Matías Fuentes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("tecnico@teqmed.cl", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Técnico", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Divider()

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Jefatura directa", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("Paula Riquelme", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Tarjeta corporativa", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("Asignada", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }

                Column {
                    Text("Centros de costo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Text("CC-SERV-CONCE (por defecto)\nCC-SERV-SUR", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Permisos del Dispositivo
        Text("PERMISOS DEL DISPOSITIVO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Fila Permiso Cámara (Abre el aviso si no hay permiso)
                FilaPermisoInteractivo(
                    icono = "📷",
                    titulo = "Cámara",
                    subtitulo = "Fotografiar comprobantes",
                    concedido = permisoCamaraConcedido,
                    onClick = {
                        if (!permisoCamaraConcedido) mostrarAvisoCamara = true
                    }
                )

                Divider()

                // Fila Permiso Ubicación (Abre el aviso si no hay permiso)
                FilaPermisoInteractivo(
                    icono = "📍",
                    titulo = "Ubicación",
                    subtitulo = "Registrar dónde ocurrió el gasto",
                    concedido = permisoUbicacionConcedido,
                    onClick = {
                        if (!permisoUbicacionConcedido) mostrarAvisoUbicacion = true
                    }
                )
            }
        }

        // Preferencias
        Text("PREFERENCIAS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tema oscuro", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Switch(checked = temaOscuro, onCheckedChange = { temaOscuro = it })
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón Cerrar Sesión
        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text("🚪 Cerrar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun FilaPermisoInteractivo(
    icono: String,
    titulo: String,
    subtitulo: String,
    concedido: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !concedido, onClick = onClick) // Se deshabilita el click si ya está concedido
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icono, fontSize = 24.sp)
            Column {
                Text(titulo, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
        }

        if (concedido) {
            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                Text("✓ Permitido", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color(0xFF2E7D32), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        } else {
            Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp)) {
                Text("Solicitar", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}