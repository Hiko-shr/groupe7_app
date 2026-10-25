package com.example.groupe7app.viewmodel

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.entity.Categoria
import com.example.groupe7app.model.entity.Gasto
import com.example.groupe7app.model.enums.EstadoGasto
import com.example.groupe7app.model.enums.MedioPago
import com.example.groupe7app.model.enums.TipoDocumento
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.repository.CatalogoRepository
import com.example.groupe7app.model.repository.GastoRepository
import com.example.groupe7app.model.repository.RendicionRepository
import com.example.groupe7app.model.repository.UsuarioRepository
import com.example.groupe7app.model.rules.ResultadoTope
import com.example.groupe7app.model.rules.TopeCalculator
import com.example.groupe7app.model.validation.GastoValidator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class GastoViewModel(
    private val session: SessionManager,
    private val usuarioRepo: UsuarioRepository,
    private val rendicionRepo: RendicionRepository,
    private val gastoRepo: GastoRepository,
    private val catalogoRepo: CatalogoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(GastoUiState())
    val estado: StateFlow<GastoUiState> = _estado.asStateFlow()

    private var rendicionId = 0L
    private var usuarioId = 0L
    private var original: Gasto? = null          // el gasto de la base, si se está editando
    private var intentoGuardar = false
    private var trabajoTope: Job? = null

    // ---------- Carga inicial ----------
    // gastoId = null → gasto nuevo; con id → se edita ese gasto
    fun cargar(rendicionId: Long, gastoId: Long?) {
        if (this.rendicionId != 0L) return          // ya cargado (por ejemplo, al girar la pantalla)
        this.rendicionId = rendicionId

        viewModelScope.launch {
            catalogoRepo.categorias.collect { lista -> _estado.update { it.copy(categorias = lista) } }
        }
        viewModelScope.launch {
            catalogoRepo.centrosCosto.collect { lista -> _estado.update { it.copy(centros = lista) } }
        }
        viewModelScope.launch {
            usuarioId = session.usuarioId.first() ?: return@launch
            val usuario = usuarioRepo.obtener(usuarioId).first()
            val rendicion = rendicionRepo.obtener(rendicionId).first()
            val gasto = gastoId?.let { gastoRepo.obtener(it) }
            original = gasto

            if (gasto == null) {
                // Nuevo: el centro de costo parte igual al de la rendición
                _estado.update {
                    it.copy(tieneTarjeta = usuario?.tieneTarjeta == true, centroCosto = rendicion?.centroCosto)
                }
            } else {
                val categoria = catalogoRepo.categoria(gasto.categoriaId)
                _estado.update {
                    it.copy(
                        gastoId = gasto.id,
                        tieneTarjeta = usuario?.tieneTarjeta == true,
                        categoria = categoria,
                        fotoUri = gasto.rutaFoto.ifBlank { null },
                        fechaDocumento = gasto.fechaDocumento,
                        tipoDocumento = gasto.tipoDocumento,
                        rutEmisor = gasto.rutEmisor ?: "",
                        folio = gasto.folio ?: "",
                        proveedor = gasto.proveedor,
                        monto = gasto.monto.toString(),
                        medioPago = gasto.medioPago,
                        centroCosto = gasto.centroCosto,
                        descripcion = gasto.descripcion,
                        ordenTrabajo = gasto.ordenTrabajo ?: "",
                        justificacion = gasto.justificacion ?: "",
                        latitud = gasto.latitud,
                        longitud = gasto.longitud,
                        comentarioJefatura = gasto.comentarioRevision.takeIf { gasto.estado == EstadoGasto.OBSERVADO },
                        respuesta = gasto.respuestaTecnico ?: "",
                        // Solo se modifica si está en borrador o si la jefatura lo observó
                        editable = gasto.estado == EstadoGasto.BORRADOR || gasto.estado == EstadoGasto.OBSERVADO
                    )
                }
                recalcularTope()
            }
        }
    }

    // ---------- Cambios de los campos ----------
    fun onCategoriaChange(valor: Categoria) { actualizar { it.copy(categoria = valor) }; recalcularTope() }
    fun onFechaChange(valor: LocalDate) { actualizar { it.copy(fechaDocumento = valor) }; recalcularTope() }
    fun onMontoChange(valor: String) {
        actualizar { it.copy(monto = valor.filter { c -> c.isDigit() }.take(9)) }   // solo números
        recalcularTope()
    }
    fun onFotoChange(ruta: String) = actualizar { it.copy(fotoUri = ruta) }
    fun onTipoDocumentoChange(valor: TipoDocumento) = actualizar { it.copy(tipoDocumento = valor) }
    fun onRutChange(valor: String) =
        actualizar { it.copy(rutEmisor = valor.filter { c -> c.isDigit() || c in ".-kK" }.uppercase().take(12)) }
    fun onFolioChange(valor: String) = actualizar { it.copy(folio = valor.filter { c -> c.isDigit() }.take(10)) }
    fun onProveedorChange(valor: String) = actualizar { it.copy(proveedor = valor) }
    fun onMedioPagoChange(valor: MedioPago) = actualizar { it.copy(medioPago = valor) }
    fun onCentroCostoChange(valor: String) = actualizar { it.copy(centroCosto = valor) }
    fun onDescripcionChange(valor: String) = actualizar { it.copy(descripcion = valor) }
    fun onOrdenTrabajoChange(valor: String) = actualizar { it.copy(ordenTrabajo = valor) }
    fun onJustificacionChange(valor: String) = actualizar { it.copy(justificacion = valor) }
    fun onRespuestaChange(valor: String) = actualizar { it.copy(respuesta = valor) }

    // ---------- Ubicación (la pantalla obtiene el GPS y avisa aquí) ----------
    fun onBuscandoUbicacion() =
        _estado.update { it.copy(buscandoUbicacion = true, mensajeUbicacion = null) }

    fun onUbicacion(latitud: Double, longitud: Double) =
        _estado.update { it.copy(latitud = latitud, longitud = longitud, buscandoUbicacion = false, mensajeUbicacion = null) }

    fun onUbicacionError(mensaje: String) =
        _estado.update { it.copy(buscandoUbicacion = false, mensajeUbicacion = mensaje) }

    // ---------- Guardar ----------
    fun onGuardar() {
        intentoGuardar = true
        viewModelScope.launch {
            // 1) Tope actualizado (necesita consultar la base para el tope diario)
            val tope = calcularTope(_estado.value)
            _estado.update { it.copy(tope = tope) }
            val actual = _estado.value

            // 2) Validaciones del formulario
            var errores = validar(actual)

            // 3) Duplicado RUT + folio (RN-02): se consulta la base
            if (errores.rut == null && errores.folio == null && GastoValidator.requiereRut(actual.tipoDocumento)) {
                val rendicionDuplicada = gastoRepo.rendicionDelDuplicado(
                    GastoValidator.normalizarRut(actual.rutEmisor), actual.folio.trim(), actual.gastoId ?: 0
                )
                if (rendicionDuplicada != null) {
                    errores = errores.copy(folio = "Este comprobante ya está rendido en $rendicionDuplicada")
                }
            }

            _estado.update { it.copy(errores = errores) }
            if (errores.hayErrores()) return@launch

            // 4) Guardar en Room
            _estado.update { it.copy(guardando = true) }
            try {
                gastoRepo.guardar(construirGasto(actual, tope))
                _estado.update { it.copy(guardando = false, guardado = true) }
            } catch (e: SQLiteConstraintException) {
                // Respaldo: el índice único de la tabla también impide el duplicado
                _estado.update {
                    it.copy(guardando = false, errores = it.errores.copy(folio = "Este comprobante ya fue rendido"))
                }
            }
        }
    }

    // ---------- Funciones de apoyo ----------
    private fun actualizar(cambio: (GastoUiState) -> GastoUiState) {
        _estado.update { actual ->
            val nuevo = cambio(actual)
            if (intentoGuardar) nuevo.copy(errores = validar(nuevo)) else nuevo
        }
    }

    // Cada vez que cambia la categoría, el monto o la fecha se recalcula el tope.
    // Si el usuario escribe rápido, se cancela el cálculo anterior.
    private fun recalcularTope() {
        trabajoTope?.cancel()
        trabajoTope = viewModelScope.launch {
            val tope = calcularTope(_estado.value)
            actualizar { it.copy(tope = tope) }
        }
    }

    private suspend fun calcularTope(e: GastoUiState): ResultadoTope? {
        val categoria = e.categoria ?: return null
        val monto = e.monto.toIntOrNull() ?: return null
        // Para el tope diario se suma lo que el técnico ya gastó ese día en esa categoría
        val acumulado = if (categoria.topeDiario != null)
            gastoRepo.acumuladoDelDia(usuarioId, categoria.id, e.fechaDocumento, e.gastoId ?: 0)
        else 0
        return TopeCalculator.calcular(categoria, monto, acumulado)
    }

    private fun validar(e: GastoUiState) = GastoErrores(
        categoria = GastoValidator.validarCategoria(e.categoria),
        foto = GastoValidator.validarFoto(e.fotoUri),
        fecha = GastoValidator.validarFecha(e.fechaDocumento),
        rut = GastoValidator.validarRut(e.tipoDocumento, e.rutEmisor),
        folio = GastoValidator.validarFolio(e.tipoDocumento, e.folio),
        proveedor = GastoValidator.validarProveedor(e.proveedor),
        monto = GastoValidator.validarMonto(e.monto),
        medioPago = GastoValidator.validarMedioPago(e.medioPago, e.tieneTarjeta),
        centroCosto = GastoValidator.validarCentroCosto(e.centroCosto),
        descripcion = GastoValidator.validarDescripcion(e.descripcion),
        justificacion = GastoValidator.validarJustificacion(e.tope, e.justificacion),
        respuesta = GastoValidator.validarRespuesta(e.comentarioJefatura != null, e.respuesta)
    )

    private fun GastoErrores.hayErrores() = listOfNotNull(
        categoria, foto, fecha, rut, folio, proveedor, monto, medioPago,
        centroCosto, descripcion, justificacion, respuesta
    ).isNotEmpty()

    // Arma la entidad Gasto. Si se está editando, parte del original para no perder
    // los campos que esta pantalla no muestra (estado, comentarios de la jefatura, etc.)
    private fun construirGasto(e: GastoUiState, tope: ResultadoTope?): Gasto {
        val conRut = GastoValidator.requiereRut(e.tipoDocumento)
        val monto = e.monto.toInt()                 // seguro: el validador ya revisó que sea número
        val base = original ?: Gasto(
            rendicionId = rendicionId, categoriaId = 0, fechaDocumento = e.fechaDocumento,
            tipoDocumento = e.tipoDocumento, proveedor = "", monto = 0, montoAdmitido = 0,
            medioPago = e.medioPago!!, centroCosto = "", descripcion = "", rutaFoto = ""
        )
        return base.copy(
            categoriaId = e.categoria!!.id,
            fechaDocumento = e.fechaDocumento,
            tipoDocumento = e.tipoDocumento,
            rutEmisor = if (conRut) GastoValidator.normalizarRut(e.rutEmisor) else null,
            folio = if (conRut) e.folio.trim() else null,
            proveedor = e.proveedor.trim(),
            monto = monto,
            montoAdmitido = tope?.admitido ?: monto,
            justificacion = e.justificacion.trim().takeIf { tope?.requiereJustificacion == true },
            medioPago = e.medioPago!!,
            centroCosto = e.centroCosto!!,
            descripcion = e.descripcion.trim(),
            ordenTrabajo = e.ordenTrabajo.trim().ifBlank { null },
            rutaFoto = e.fotoUri!!,
            latitud = e.latitud,
            longitud = e.longitud,
            respuestaTecnico = if (e.comentarioJefatura != null) e.respuesta.trim() else base.respuestaTecnico
        )
    }
}
