package com.example.groupe7app.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color corporativo TeqGastos de la captura
val TeqDarkBlue = Color(0xFF0F4C5C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    correo: String,
    onCorreoChange: (String) -> Unit,
    errorCorreo: String?,
    contrasena: String,
    onContrasenaChange: (String) -> Unit,
    errorContrasena: String?,
    cargando: Boolean,
    onIngresarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var contrasenaVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // --- CABECERA AZUL CORPORATIVA ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TeqDarkBlue)
                .padding(vertical = 40.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Contenedor del Isotipo
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📄",
                        fontSize = 28.sp
                    )
                }

                Text(
                    text = "TeqGastos",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Rendición de gastos en terreno - TEQMED SpA",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // --- FORMULARIO Y CONTENIDO ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // CAMPO CORREO
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Correo corporativo",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (errorCorreo != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = correo,
                    onValueChange = onCorreoChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = errorCorreo != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    trailingIcon = {
                        if (errorCorreo != null) {
                            Text(
                                text = "!",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                AnimatedVisibility(visible = errorCorreo != null) {
                    Text(
                        text = errorCorreo ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // CAMPO CONTRASEÑA
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Contraseña",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = onContrasenaChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = errorContrasena != null,
                    visualTransformation = if (contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                            Text(
                                text = if (contrasenaVisible) "👁" else "🙈",
                                fontSize = 16.sp
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorContrasena == null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "✓ ",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Mínimo 8 caracteres",
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    Text(
                        text = errorContrasena,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÓN INGRESAR
            Button(
                onClick = onIngresarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !cargando,
                colors = ButtonDefaults.buttonColors(containerColor = TeqDarkBlue),
                shape = RoundedCornerShape(26.dp)
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Ingresar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TARJETA USUARIOS DE PRUEBA
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "USUARIOS DE PRUEBA (PRECARGADOS)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline
                    )

                    FilaUsuarioPrueba("tecnico@teqmed.cl", "Técnico")
                    FilaUsuarioPrueba("jefatura@teqmed.cl", "Jefatura")
                    FilaUsuarioPrueba("finanzas@teqmed.cl", "Finanzas")
                }
            }
        }
    }
}

@Composable
private fun FilaUsuarioPrueba(correo: String, rol: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = correo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = rol,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}