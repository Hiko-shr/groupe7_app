package com.example.groupe7app.model.repository

import androidx.room.withTransaction
import com.example.groupe7app.model.database.AppDatabase
import com.example.groupe7app.model.entity.HistorialEstado
import com.example.groupe7app.model.entity.Pago
import com.example.groupe7app.model.entity.Rendicion
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.remote.AccionRequest
import com.example.groupe7app.model.remote.CambioEstadoRequest
import com.example.groupe7app.model.remote.RendicionesApi
import com.example.groupe7app.model.remote.UsuariosApi
import com.example.groupe7app.model.remote.aEntidad
import com.example.groupe7app.model.remote.aRequest
import com.example.groupe7app.model.remote.comoLista
import com.example.groupe7app.model.remote.exigirExito
import kotlinx.coroutines.flow.Flow

// Lecturas: Room al tiro + refresco desde ms-rendiciones (refrescandoCon).
// Escrituras: primero el servidor (que valida y guarda en MySQL); si responde bien, se actualiza Room.
// Si el servidor rechaza (400/409) o no hay conexión, la excepción sube al ViewModel.
class RendicionRepository(
    private val db: AppDatabase,
    private val api: RendicionesApi,
    private val usuariosApi: UsuariosApi
) {

    private val rendicionDao = db.rendicionDao()
    private val gastoDao = db.gastoDao()
    private val historialDao = db.historialDao()
    private val pagoDao = db.pagoDao()
    private val usuarioDao = db.usuarioDao()

    // ---------- Lecturas ----------
    fun misRendiciones(usuarioId: Long): Flow<List<Rendicion>> =
        rendicionDao.misRendiciones(usuarioId).refrescandoCon {
            val lista = api.rendiciones(usuarioId = usuarioId).map { it.aEntidad() }
            db.withTransaction {
                rendicionDao.eliminarDeUsuarioExcepto(usuarioId, lista.map { it.id })
                rendicionDao.guardarTodas(lista)
            }
        }

    fun obtener(id: Long): Flow<Rendicion?> =
        rendicionDao.obtener(id).refrescandoCon { rendicionDao.guardar(api.rendicion(id).aEntidad()) }

    fun historial(rendicionId: Long): Flow<List<HistorialEstado>> =
        historialDao.deRendicion(rendicionId).refrescandoCon {
            val registros = api.historial(rendicionId).map { it.aEntidad() }
            db.withTransaction {
                historialDao.eliminarDeRendicionExcepto(rendicionId, registros.map { it.id })
                historialDao.guardarTodos(registros)
            }
        }

    // Los usuarios viven en otro microservicio: primero se piden los técnicos del equipo
    // a ms-usuarios, y con sus ids se piden las rendiciones a ms-rendiciones
    fun bandejaJefatura(jefaturaId: Long): Flow<List<Rendicion>> =
        rendicionDao.bandejaJefatura(jefaturaId).refrescandoCon {
            val equipo = usuariosApi.listar(jefaturaId).map { it.aEntidad() }
            usuarioDao.guardarTodos(equipo)                      // la consulta de Room los necesita (JOIN)
            val ids = equipo.map { it.id }
            val lista = if (ids.isEmpty()) emptyList()
                        else api.rendiciones(estado = EstadoRendicion.ENVIADA, usuarioIds = ids.comoLista()).map { it.aEntidad() }
            db.withTransaction {
                rendicionDao.eliminarEnviadasDeEquipoExcepto(ids, lista.map { it.id })
                rendicionDao.guardarTodas(lista)
            }
        }

    fun porEstado(estado: EstadoRendicion): Flow<List<Rendicion>> =
        rendicionDao.porEstado(estado).refrescandoCon {
            val lista = api.rendiciones(estado = estado).map { it.aEntidad() }
            db.withTransaction {
                rendicionDao.eliminarEnEstadoExcepto(estado, lista.map { it.id })
                rendicionDao.guardarTodas(lista)
            }
        }

    // ---------- Técnico ----------
    // El servidor asigna el número RND y anota "Creada" en el historial
    suspend fun crear(rendicion: Rendicion): Long {
        val creada = api.crearRendicion(rendicion.aRequest()).aEntidad()
        rendicionDao.guardar(creada)
        return creada.id
    }

    suspend fun actualizar(rendicion: Rendicion) {
        rendicionDao.guardar(api.actualizarRendicion(rendicion.id, rendicion.aRequest()).aEntidad())
    }

    suspend fun eliminar(rendicion: Rendicion) {
        api.eliminarRendicion(rendicion.id).exigirExito()       // MySQL borra en cascada sus gastos
        db.withTransaction {
            gastoDao.eliminarDeRendicionExcepto(rendicion.id, emptyList())   // = borrar todos sus gastos locales
            rendicionDao.eliminar(rendicion)
        }
    }

    suspend fun enviar(rendicion: Rendicion, usuarioId: Long, comentario: String? = null) {
        rendicionDao.guardar(api.enviar(rendicion.id, AccionRequest(usuarioId, comentario)).aEntidad())
        sincronizarGastos(rendicion.id)                         // sus gastos pasaron a ENVIADO
    }

    // ---------- Jefatura y Finanzas ----------
    suspend fun aprobarFinanzas(rendicion: Rendicion, usuarioId: Long) {
        rendicionDao.guardar(api.aprobarFinanzas(rendicion.id, AccionRequest(usuarioId)).aEntidad())
        sincronizarGastos(rendicion.id)                         // sus gastos pasaron a APROBADO_FINAL
    }

    suspend fun cambiarEstado(rendicion: Rendicion, nuevo: EstadoRendicion, usuarioId: Long, comentario: String? = null) {
        rendicionDao.guardar(api.cambiarEstado(rendicion.id, CambioEstadoRequest(nuevo, usuarioId, comentario)).aEntidad())
    }

    suspend fun registrarPago(pago: Pago, rendiciones: List<Rendicion>) {
        val registrado = api.registrarPago(pago.aRequest(rendiciones.map { it.id })).aEntidad()
        pagoDao.guardar(registrado)
        rendiciones.forEach { rendicionDao.guardar(api.rendicion(it.id).aEntidad()) }   // ahora PAGADAS
    }

    private suspend fun sincronizarGastos(rendicionId: Long) {
        val gastos = api.gastos(rendicionId = rendicionId).map { it.aEntidad() }
        db.withTransaction {
            gastoDao.eliminarDeRendicionExcepto(rendicionId, gastos.map { it.id })
            gastoDao.guardarTodos(gastos)
        }
    }
}
