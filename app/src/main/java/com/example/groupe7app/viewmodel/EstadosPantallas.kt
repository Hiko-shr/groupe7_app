package com.example.groupe7app.viewmodel

import com.example.groupe7app.model.entity.Categoria
import com.example.groupe7app.model.entity.CentroCosto
import com.example.groupe7app.model.entity.Gasto
import com.example.groupe7app.model.entity.HistorialEstado
import com.example.groupe7app.model.entity.Rendicion
import com.example.groupe7app.model.entity.Usuario
import com.example.groupe7app.model.enums.MedioPago
import com.example.groupe7app.model.enums.MotivoRechazo
import com.example.groupe7app.model.enums.TipoDocumento
import com.example.groupe7app.model.enums.TipoRendicion
import com.example.groupe7app.model.rules.ResultadoTope
import java.time.LocalDate

// ---------- Clases de apoyo para mostrar ----------
data class RendicionResumen(val rendicion: Rendicion, val cantidadGastos: Int, val total: Int)
data class GastoConCategoria(val gasto: Gasto, val categoria: String)

// ---------- Técnico ----------
data class ResumenMes(
    val enBorrador: Int = 0, val enAprobacion: Int = 0,
    val porReembolsar: Int = 0, val reembolsado: Int = 0
)

data class InicioTecnicoUiState(
    val nombre: String = "",
    val resumen: ResumenMes = ResumenMes(),
    val observadas: List<RendicionResumen> = emptyList(),
    val enCurso: RendicionResumen? = null,
    val cargando: Boolean = true
)

enum class FiltroEstado(val etiqueta: String) {
    TODAS("Todas"), BORRADOR("Borrador"), EN_APROBACION("En aprobación"), OBSERVADAS("Observadas")
}

data class MisRendicionesUiState(
    val rendiciones: List<RendicionResumen> = emptyList(),
    val filtro: FiltroEstado = FiltroEstado.TODAS,
    val cargando: Boolean = true
)

data class RendicionErrores(
    val titulo: String? = null, val destino: String? = null, val periodo: String? = null,
    val centroCosto: String? = null, val observacion: String? = null
)

data class NuevaRendicionUiState(
    val titulo: String = "",
    val tipo: TipoRendicion = TipoRendicion.GASTOS_GENERALES,
    val destino: String = "",
    val desde: LocalDate? = null,
    val hasta: LocalDate? = null,
    val centroCosto: String? = null,
    val centros: List<CentroCosto> = emptyList(),
    val observacion: String = "",
    val errores: RendicionErrores = RendicionErrores(),
    val guardando: Boolean = false,
    val guardadaId: Long? = null
)

data class DetalleRendicionUiState(
    val rendicion: Rendicion? = null,
    val gastos: List<GastoConCategoria> = emptyList(),
    val totalesPorMedio: Map<MedioPago, Int> = emptyMap(),
    val total: Int = 0,
    val exceso: Int = 0,
    val reembolsoEstimado: Int = 0,
    val historial: List<HistorialEstado> = emptyList(),
    val editable: Boolean = false,
    val mostrarConfirmacion: Boolean = false,
    val errorEnvio: String? = null,
    val enviada: Boolean = false
)

data class GastoErrores(
    val categoria: String? = null, val foto: String? = null, val fecha: String? = null,
    val rut: String? = null, val folio: String? = null, val proveedor: String? = null,
    val monto: String? = null, val medioPago: String? = null, val centroCosto: String? = null,
    val descripcion: String? = null, val justificacion: String? = null
)

data class GastoUiState(
    val categorias: List<Categoria> = emptyList(),
    val categoria: Categoria? = null,
    val fotoUri: String? = null,
    val fechaDocumento: LocalDate = LocalDate.now(),
    val tipoDocumento: TipoDocumento = TipoDocumento.BOLETA,
    val rutEmisor: String = "",
    val folio: String = "",
    val proveedor: String = "",
    val monto: String = "",
    val medioPago: MedioPago? = null,
    val tieneTarjeta: Boolean = false,
    val centroCosto: String? = null,
    val centros: List<CentroCosto> = emptyList(),
    val descripcion: String = "",
    val ordenTrabajo: String = "",
    val justificacion: String = "",
    val tope: ResultadoTope? = null,
    val ubicacion: String? = null,
    val buscandoUbicacion: Boolean = false,
    val errores: GastoErrores = GastoErrores(),
    val guardando: Boolean = false,
    val guardado: Boolean = false
)

data class ObservacionUiState(
    val rendicion: Rendicion? = null,
    val gastosObservados: List<GastoConCategoria> = emptyList(),
    val respuestas: Map<Long, String> = emptyMap(),
    val errores: Map<Long, String> = emptyMap(),
    val enviando: Boolean = false,
    val reenviada: Boolean = false
)

// ---------- Jefatura ----------
data class RendicionPendiente(
    val rendicion: Rendicion, val tecnico: String, val cantidadGastos: Int,
    val total: Int, val alertas: Int, val diasEspera: Int
)

data class BandejaJefaturaUiState(
    val pendientes: List<RendicionPendiente> = emptyList(),
    val cargando: Boolean = true
)

data class RevisionRendicionUiState(
    val rendicion: Rendicion? = null,
    val tecnico: String = "",
    val gastos: List<GastoConCategoria> = emptyList(),
    val resueltos: Int = 0,
    val puedeFinalizar: Boolean = false,
    val mostrarConfirmacion: Boolean = false,
    val finalizada: Boolean = false
)

enum class Decision(val etiqueta: String) {
    APROBAR("Aprobar"), OBSERVAR("Observar"), RECHAZAR("Rechazar")
}

data class ResolucionErrores(
    val decision: String? = null, val montoAprobado: String? = null,
    val comentario: String? = null, val motivo: String? = null
)

data class RevisionGastoUiState(
    val gasto: Gasto? = null,
    val categoria: String = "",
    val tecnico: String = "",
    val decision: Decision? = null,
    val montoAprobado: String = "",
    val comentario: String = "",
    val motivo: MotivoRechazo? = null,
    val errores: ResolucionErrores = ResolucionErrores(),
    val guardando: Boolean = false,
    val guardado: Boolean = false
)

// ---------- Comunes ----------
data class PerfilUiState(
    val usuario: Usuario? = null,
    val nombreJefatura: String? = null,
    val temaOscuro: Boolean? = null,
    val permisoCamara: Boolean = false,
    val permisoUbicacion: Boolean = false
)