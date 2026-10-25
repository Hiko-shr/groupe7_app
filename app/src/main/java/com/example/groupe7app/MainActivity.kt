package com.example.groupe7app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.groupe7app.model.entity.CentroCosto
import com.example.groupe7app.model.entity.Rendicion
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.enums.TipoRendicion
import com.example.groupe7app.ui.components.BottomNavigationBar
import com.example.groupe7app.ui.screens.login.LoginContent
import com.example.groupe7app.ui.screens.perfil.PerfilContent
import com.example.groupe7app.ui.screens.tecnico.*
import com.example.groupe7app.ui.theme.Groupe7AppTheme
import com.example.groupe7app.viewmodel.FiltroEstado
import com.example.groupe7app.viewmodel.InicioTecnicoUiState
import com.example.groupe7app.viewmodel.MisRendicionesUiState
import com.example.groupe7app.viewmodel.RendicionResumen
import com.example.groupe7app.viewmodel.ResumenMes
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Groupe7AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavegacionConPersistenciaYValidacion()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavegacionConPersistenciaYValidacion() {
    // 1. Navegación
    var pantallaActual by remember { mutableStateOf("login") }

    // Control del botón/gesto atrás del sistema Android
    BackHandler(enabled = pantallaActual != "login") {
        when (pantallaActual) {
            "inicio" -> pantallaActual = "login"
            "rendiciones", "perfil", "nueva_rendicion", "nuevo_gasto" -> pantallaActual = "inicio"
        }
    }

    // 2. Estados de Login
    var correo by remember { mutableStateOf("tecnico@teqmed.cl") }
    var contrasena by remember { mutableStateOf("12345678") }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorContrasena by remember { mutableStateOf<String?>(null) }

    // Listas base
    val listaCentros = remember {
        listOf(
            CentroCosto("CC01", "CC-SERV-CONCE - Servicio técnico Conce"),
            CentroCosto("CC02", "CC-SERV-SUR - Servicio técnico Sur")
        )
    }

    val listaCategorias = remember {
        listOf(
            CategoriaGastoItem(1L, "Alimentación"),
            CategoriaGastoItem(2L, "Combustible"),
            CategoriaGastoItem(3L, "Peaje"),
            CategoriaGastoItem(4L, "Alojamiento"),
            CategoriaGastoItem(5L, "Estacionamiento"),
            CategoriaGastoItem(6L, "Otros")
        )
    }

    // 3. BASE DE DATOS EN MEMORIA (Reactiva y Corregida a tus Entidades)
    val listaRendiciones = remember {
        mutableStateListOf(
            RendicionResumen(
                rendicion = Rendicion(
                    id = 1L,
                    numero = "RND-2026-000214",
                    titulo = "Comisión Los Ángeles - autoclaves",
                    estado = EstadoRendicion.BORRADOR,
                    tipo = TipoRendicion.COMISION_SERVICIO,
                    destino = "Los Ángeles",
                    usuarioId = 1L,
                    desde = LocalDate.of(2026, 10, 1),
                    hasta = LocalDate.of(2026, 10, 5),
                    centroCosto = listaCentros.first().nombre
                ),
                total = 149604,
                cantidadGastos = 6
            ),
            RendicionResumen(
                rendicion = Rendicion(
                    id = 2L,
                    numero = "RND-2026-000207",
                    titulo = "Gran Concepción - fin de septiembre",
                    estado = EstadoRendicion.OBSERVADA,
                    tipo = TipoRendicion.GASTOS_GENERALES,
                    destino = "Concepción",
                    usuarioId = 1L,
                    desde = LocalDate.of(2026, 9, 24),
                    hasta = LocalDate.of(2026, 9, 30),
                    centroCosto = listaCentros.first().nombre
                ),
                total = 38150,
                cantidadGastos = 4
            )
        )
    }

    // Formularios - Filtro de lista
    var filtroSeleccionado by remember { mutableStateOf(FiltroEstado.TODAS) }

    // Formularios - Nueva Rendición
    var tituloRendicion by remember { mutableStateOf("") }
    var errorTitulo by remember { mutableStateOf<String?>(null) }
    var tipoRendicion by remember { mutableStateOf<TipoRendicion?>(TipoRendicion.COMISION_SERVICIO) }
    var destinoRendicion by remember { mutableStateOf("") }
    var centroRendicion by remember { mutableStateOf<CentroCosto?>(listaCentros.firstOrNull()) }

    // Formularios - Nuevo Gasto
    var montoGasto by remember { mutableStateOf("") }
    var errorMonto by remember { mutableStateOf<String?>(null) }

    var descripcionGasto by remember { mutableStateOf("") }
    var errorDescripcion by remember { mutableStateOf<String?>(null) }

    var proveedorGasto by remember { mutableStateOf("") }
    var numeroComprobante by remember { mutableStateOf("") }
    var categoriaGasto by remember { mutableStateOf<CategoriaGastoItem?>(listaCategorias.firstOrNull()) }

    val mostrarBottomBar = pantallaActual in listOf("inicio", "rendiciones", "perfil")

    Scaffold(
        bottomBar = {
            if (mostrarBottomBar) {
                BottomNavigationBar(
                    rutaActual = pantallaActual,
                    onNavegar = { nuevaRuta -> pantallaActual = nuevaRuta }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (pantallaActual) {
                // --- LOGIN ---
                "login" -> {
                    LoginContent(
                        correo = correo,
                        onCorreoChange = {
                            correo = it
                            errorCorreo = if (it.contains("@teqmed.cl")) null else "Debe ser correo @teqmed.cl"
                        },
                        errorCorreo = errorCorreo,
                        contrasena = contrasena,
                        onContrasenaChange = {
                            contrasena = it
                            errorContrasena = if (it.length >= 8) null else "Mínimo 8 caracteres"
                        },
                        errorContrasena = errorContrasena,
                        cargando = false,
                        onIngresarClick = {
                            if (correo.contains("@teqmed.cl") && contrasena.length >= 8) {
                                pantallaActual = "inicio"
                            } else {
                                if (!correo.contains("@teqmed.cl")) errorCorreo = "Debe ser correo @teqmed.cl"
                                if (contrasena.length < 8) errorContrasena = "Mínimo 8 caracteres"
                            }
                        }
                    )
                }

                // --- INICIO ---
                "inicio" -> {
                    val totalBorrador = listaRendiciones
                        .filter { it.rendicion.estado == EstadoRendicion.BORRADOR }
                        .sumOf { it.total }

                    InicioTecnicoContent(
                        estado = InicioTecnicoUiState(
                            nombre = "Matías Fuentes",
                            cargando = false,
                            resumen = ResumenMes(
                                enBorrador = totalBorrador,
                                enAprobacion = 38150,
                                porReembolsar = 0,
                                reembolsado = 46300
                            ),
                            observadas = listaRendiciones.filter { it.rendicion.estado == EstadoRendicion.OBSERVADA },
                            enCurso = listaRendiciones.firstOrNull { it.rendicion.estado == EstadoRendicion.BORRADOR }
                        ),
                        onVerRendiciones = { pantallaActual = "rendiciones" },
                        onAbrirRendicion = { pantallaActual = "nueva_rendicion" },
                        onNuevoGasto = { pantallaActual = "nuevo_gasto" }
                    )
                }

                // --- MIS RENDICIONES ---
                "rendiciones" -> {
                    MisRendicionesContent(
                        estado = MisRendicionesUiState(
                            filtro = filtroSeleccionado,
                            rendiciones = listaRendiciones,
                            cargando = false
                        ),
                        onFiltro = { nuevoFiltro -> filtroSeleccionado = nuevoFiltro },
                        onAbrirRendicion = { pantallaActual = "nueva_rendicion" },
                        onNuevaRendicion = { pantallaActual = "nueva_rendicion" }
                    )
                }

                // --- PERFIL ---
                "perfil" -> {
                    PerfilContent(onCerrarSesion = { pantallaActual = "login" })
                }

                // --- NUEVA RENDICIÓN ---
                "nueva_rendicion" -> {
                    NuevaRendicionContent(
                        titulo = tituloRendicion,
                        onTituloChange = {
                            tituloRendicion = it
                            errorTitulo = if (it.isBlank()) "El título es obligatorio" else null
                        },
                        errorTitulo = errorTitulo,
                        tipoSeleccionado = tipoRendicion,
                        onTipoChange = { tipoRendicion = it },
                        destino = destinoRendicion,
                        onDestinoChange = { destinoRendicion = it },
                        fechaDesde = LocalDate.now(),
                        fechaHasta = LocalDate.now().plusDays(4),
                        onAbrirSelectorFecha = {},
                        centrosCosto = listaCentros,
                        centroSeleccionado = centroRendicion,
                        onCentroChange = { centroRendicion = it },
                        onGuardarClick = {
                            if (tituloRendicion.isBlank()) {
                                errorTitulo = "El título es obligatorio"
                            } else {
                                val nuevaRendicionObj = Rendicion(
                                    id = (listaRendiciones.size + 1).toLong(),
                                    numero = "RND-2026-000${215 + listaRendiciones.size}",
                                    titulo = tituloRendicion,
                                    estado = EstadoRendicion.BORRADOR,
                                    tipo = tipoRendicion ?: TipoRendicion.GASTOS_GENERALES,
                                    destino = destinoRendicion.ifBlank { "Destino general" },
                                    usuarioId = 1L,
                                    desde = LocalDate.now(),
                                    hasta = LocalDate.now().plusDays(4),
                                    centroCosto = (centroRendicion ?: listaCentros.first()).nombre
                                )
                                val nueva = RendicionResumen(
                                    rendicion = nuevaRendicionObj,
                                    total = 0,
                                    cantidadGastos = 0
                                )
                                listaRendiciones.add(0, nueva)

                                tituloRendicion = ""
                                destinoRendicion = ""
                                errorTitulo = null

                                pantallaActual = "rendiciones"
                            }
                        },
                        onVolverClick = { pantallaActual = "inicio" }
                    )
                }

                // --- NUEVO GASTO ---
                "nuevo_gasto" -> {
                    GastoContent(
                        monto = montoGasto,
                        onMontoChange = {
                            montoGasto = it
                            errorMonto = if (it.toIntOrNull() != null && it.toInt() > 0) null else "Monto inválido"
                        },
                        errorMonto = errorMonto,
                        descripcion = descripcionGasto,
                        onDescripcionChange = {
                            descripcionGasto = it
                            errorDescripcion = if (it.isBlank()) "Ingresa una descripción" else null
                        },
                        errorDescripcion = errorDescripcion,
                        fecha = LocalDate.now(),
                        onAbrirSelectorFecha = {},
                        categorias = listaCategorias,
                        categoriaSeleccionada = categoriaGasto,
                        onCategoriaChange = { categoriaGasto = it },
                        proveedor = proveedorGasto,
                        onProveedorChange = { proveedorGasto = it },
                        numeroComprobante = numeroComprobante,
                        onNumeroComprobanteChange = { numeroComprobante = it },
                        onGuardarClick = {
                            val montoInt = montoGasto.toIntOrNull() ?: 0
                            var esValido = true

                            if (montoInt <= 0) {
                                errorMonto = "Monto inválido"
                                esValido = false
                            }
                            if (descripcionGasto.isBlank()) {
                                errorDescripcion = "Ingresa una descripción"
                                esValido = false
                            }

                            if (esValido && listaRendiciones.isNotEmpty()) {
                                val indiceBorrador = listaRendiciones.indexOfFirst { it.rendicion.estado == EstadoRendicion.BORRADOR }

                                if (indiceBorrador != -1) {
                                    val actual = listaRendiciones[indiceBorrador]
                                    val resumenActualizado = actual.copy(
                                        total = actual.total + montoInt,
                                        cantidadGastos = actual.cantidadGastos + 1
                                    )

                                    listaRendiciones[indiceBorrador] = resumenActualizado

                                    montoGasto = ""
                                    descripcionGasto = ""
                                    proveedorGasto = ""
                                    numeroComprobante = ""
                                    errorMonto = null
                                    errorDescripcion = null

                                    pantallaActual = "inicio"
                                }
                            }
                        },
                        onVolverClick = { pantallaActual = "inicio" }
                    )
                }
            }
        }
    }
}