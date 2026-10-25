package com.example.groupe7app.ui.screens.tecnico

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groupe7app.model.archivos.ArchivosFoto
import com.example.groupe7app.viewmodel.GastoViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Conecta el ViewModel con el diseño y maneja los recursos nativos:
// cámara, galería, permisos y GPS (necesitan el Context, por eso están aquí y no en el ViewModel).
@Composable
fun GastoScreen(
    viewModel: GastoViewModel,
    rendicionId: Long,
    gastoId: Long?,
    onVolver: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.cargar(rendicionId, gastoId) }

    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Al guardar, volvemos al detalle de la rendición
    LaunchedEffect(estado.guardado) { if (estado.guardado) onVolver() }

    // Qué permiso se negó (para mostrar el diálogo); null = ninguno
    var permisoNegado by remember { mutableStateOf<String?>(null) }

    // ---------- Cámara ----------
    // Ruta del archivo donde la cámara está guardando la foto (rememberSaveable: sobrevive si se recrea la pantalla)
    var rutaPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        val ruta = rutaPendiente
        if (exito && ruta != null) viewModel.onFotoChange(ruta)
    }

    fun abrirCamara() {
        val archivo = ArchivosFoto.nuevoArchivo(context)
        rutaPendiente = archivo.absolutePath
        camara.launch(ArchivosFoto.uriPara(context, archivo))
    }

    val permisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) abrirCamara() else permisoNegado = "la cámara"
    }

    // ---------- Galería ----------
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch {
                // Copiar el archivo no debe hacerse en el hilo de la interfaz
                val ruta = withContext(Dispatchers.IO) { ArchivosFoto.copiarDesdeGaleria(context, uri) }
                if (ruta != null) viewModel.onFotoChange(ruta)
            }
        }
    }

    // ---------- Ubicación ----------
    val permisoUbicacion = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultados ->
        if (resultados.values.any { it }) obtenerUbicacion(context, viewModel)
        else permisoNegado = "la ubicación"
    }

    GastoContent(
        estado = estado,
        onVolver = onVolver,
        onTomarFoto = {
            if (tienePermiso(context, Manifest.permission.CAMERA)) abrirCamara()
            else permisoCamara.launch(Manifest.permission.CAMERA)
        },
        onElegirGaleria = {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onCategoriaChange = viewModel::onCategoriaChange,
        onFechaChange = viewModel::onFechaChange,
        onTipoDocumentoChange = viewModel::onTipoDocumentoChange,
        onRutChange = viewModel::onRutChange,
        onFolioChange = viewModel::onFolioChange,
        onProveedorChange = viewModel::onProveedorChange,
        onMontoChange = viewModel::onMontoChange,
        onJustificacionChange = viewModel::onJustificacionChange,
        onMedioPagoChange = viewModel::onMedioPagoChange,
        onCentroCostoChange = viewModel::onCentroCostoChange,
        onDescripcionChange = viewModel::onDescripcionChange,
        onOrdenTrabajoChange = viewModel::onOrdenTrabajoChange,
        onRespuestaChange = viewModel::onRespuestaChange,
        onAgregarUbicacion = {
            if (tienePermiso(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                tienePermiso(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            ) obtenerUbicacion(context, viewModel)
            else permisoUbicacion.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        },
        onGuardar = viewModel::onGuardar
    )

    // ---------- Diálogo cuando se niega un permiso ----------
    permisoNegado?.let { recurso ->
        AlertDialog(
            onDismissRequest = { permisoNegado = null },
            title = { Text("Permiso necesario") },
            text = { Text("Para usar $recurso, permite el acceso en los ajustes de la aplicación.") },
            confirmButton = {
                TextButton(onClick = {
                    permisoNegado = null
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null))
                    )
                }) { Text("Abrir ajustes") }
            },
            dismissButton = { TextButton(onClick = { permisoNegado = null }) { Text("Ahora no") } }
        )
    }
}

private fun tienePermiso(context: Context, permiso: String): Boolean =
    ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED

// Pide una ubicación actual al GPS (FusedLocationProviderClient de Google Play Services)
@SuppressLint("MissingPermission")          // el permiso ya se revisó antes de llamar a esta función
private fun obtenerUbicacion(context: Context, viewModel: GastoViewModel) {
    viewModel.onBuscandoUbicacion()
    val cliente = LocationServices.getFusedLocationProviderClient(context)
    cliente.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
        .addOnSuccessListener { ubicacion ->
            if (ubicacion != null) viewModel.onUbicacion(ubicacion.latitude, ubicacion.longitude)
            else viewModel.onUbicacionError("No se pudo obtener la ubicación. Activa el GPS e inténtalo de nuevo")
        }
        .addOnFailureListener {
            viewModel.onUbicacionError("No se pudo obtener la ubicación")
        }
}
