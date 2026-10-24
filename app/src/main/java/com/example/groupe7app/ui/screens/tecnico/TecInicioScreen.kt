package com.example.groupe7app.ui.screens.tecnico

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colores extraídos del Mockup
val BgColor = Color(0xFFEEF2F5)
val PrimaryColor = Color(0xFF0E5A73)
val InkColor = Color(0xFF14212E)
val MutedColor = Color(0xFF4A5866)
val CardBg = Color(0xFFFFFFFF)
val LineColor = Color(0xFFD9E1E8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TecInicioScreen(
    onNavegarRendiciones: () -> Unit = {},
    onNavegarNuevoGasto: () -> Unit = {},
    onNavegarObservacion: () -> Unit = {},
    onNavegarDetalle: () -> Unit = {}
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BgColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBg)
                    .border(1.dp, LineColor)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hola, Matías", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = InkColor)
                    Text("Técnico · jueves 29 de octubre", fontSize = 12.5.sp, color = MutedColor)
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(PrimaryColor, CircleShape)
                        .clickable { /* Ir a perfil */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text("MF", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavegarNuevoGasto,
                containerColor = PrimaryColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nuevo gasto", fontWeight = FontWeight.ExtraBold, fontSize = 15.5.sp)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tarjeta: Alerta Observada
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCEBCF)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE6B66B)),
                modifier = Modifier.fillMaxWidth().clickable { onNavegarObservacion() }
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = "Alerta", tint = Color(0xFF7A4500))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("RND-2026-000207 observada", fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = Color(0xFF5E3500))
                        Text("1 gasto necesita tu corrección", fontSize = 14.5.sp, color = Color(0xFF5E3500))
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF7A4500))
                }
            }

            // Tarjeta: Vencen pronto
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, LineColor),
                modifier = Modifier.fillMaxWidth().clickable { onNavegarDetalle() }
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = "Vence pronto", tint = Color(0xFFA12A20))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("5 gastos vencen pronto", fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = InkColor)
                        Text("El primero vence el 31-10 (plazo de 30 días)", fontSize = 14.5.sp, color = MutedColor)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedColor)
                }
            }

            // Tarjeta: Resumen del mes (Grilla 2x2)
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, LineColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Text("OCTUBRE 2026", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = MutedColor)
                        Text("tus rendiciones del mes", fontSize = 12.5.sp, color = MutedColor)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ResumenTile("En borrador", "$ 149.604", Modifier.weight(1f))
                        ResumenTile("En aprobación", "$ 38.150", Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ResumenTile("Por reembolsar", "$ 0", Modifier.weight(1f))
                        ResumenTile("Reembolsado", "$ 46.300", Modifier.weight(1f))
                    }
                }
            }

            // Título: Rendición en curso
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("RENDICIÓN EN CURSO", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = MutedColor)
                Text("Ver todas", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryColor, modifier = Modifier.clickable { onNavegarRendiciones() })
            }

            // Tarjeta: Detalle de Rendición en curso
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, LineColor),
                modifier = Modifier.fillMaxWidth().clickable { onNavegarDetalle() }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("RND-2026-000214", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MutedColor)
                        Box(modifier = Modifier.background(Color(0xFFE6EAEE), RoundedCornerShape(99.dp)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                            Text("Borrador", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B4754))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Comisión Los Ángeles · autoclaves", fontWeight = FontWeight.Bold, fontSize = 15.5.sp, color = InkColor)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("01-10 al 05-10 · 6 gastos", fontSize = 13.5.sp, color = MutedColor)
                        Text("$ 149.604", fontWeight = FontWeight.ExtraBold, fontSize = 15.5.sp, color = InkColor)
                    }
                }
            }

            // Espacio extra al final para que el FAB no tape el contenido al hacer scroll
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

// Componente reutilizable para los cuadritos grises del resumen
@Composable
fun ResumenTile(title: String, amount: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFF4F6F8), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(title, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4A5866))
        Spacer(modifier = Modifier.height(2.dp))
        Text(amount, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF14212E))
    }
}

// Esto genera la vista previa interactiva en Android Studio
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTecInicioScreen() {
    MaterialTheme {
        TecInicioScreen()
    }
}