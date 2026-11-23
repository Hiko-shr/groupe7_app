package com.example.groupe7app.model.repository

import androidx.room.withTransaction
import com.example.groupe7app.model.database.AppDatabase
import com.example.groupe7app.model.entity.Gasto
import com.example.groupe7app.model.enums.EstadoGasto
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.enums.MotivoRechazo
import com.example.groupe7app.model.remote.RendicionesApi
import com.example.groupe7app.model.remote.ResolucionRequest
import com.example.groupe7app.model.remote.UsuariosApi
import com.example.groupe7app.model.remote.aEntidad
import com.example.groupe7app.model.remote.aRequest
import com.example.groupe7app.model.remote.comoLista
import com.example.groupe7app.model.remote.exigirExito
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GastoRepository(
    private val db: AppDatabase,
    private val api: RendicionesApi,
    private val usuariosApi: UsuariosApi
) {

    private val gastoDao = db.gastoDao()

    // ---------- Lecturas (Room al tiro + refresco desde ms-rendiciones) ----------
    fun deRendicion(rendicionId: Long): Flow<List<Gasto>> =
        gastoDao.deRendicion(rendicionId).refrescandoCon {
            val gastos = api.gastos(rendicionId = rendicionId).map { it.aEntidad() }
            db.withTransaction {
                gastoDao.eliminarDeRendicionExcepto(rendicionId, gastos.map { it.id })
                gastoDao.guardarTodos(gastos)
            }
        }

    fun deUsuario(usuarioId: Long): Flow<List<Gasto>> =
        gastoDao.deUsuario(usuarioId).refrescandoCon {
            gastoDao.guardarTodos(api.gastos(usuarioId = usuarioId).map { it.aEntidad() })
        }

    fun deEquipo(jefaturaId: Long): Flow<List<Gasto>> =
        gastoDao.deEquipo(jefaturaId).refrescandoCon {
            val ids = usuariosApi.listar(jefaturaId).map { it.id }
            if (ids.isNotEmpty())
                gastoDao.guardarTodos(api.gastos(usuarioIds = ids.comoLista()).map { it.aEntidad() })
        }

    fun deRendicionesEnEstado(estado: EstadoRendicion): Flow<List<Gasto>> =
        gastoDao.deRendicionesEnEstado(estado).refrescandoCon {
            gastoDao.guardarTodos(api.gastos(estadoRendicion = estado).map { it.aEntidad() })
        }

    // Para editar o revisar: se pide la versión del servidor; sin conexión, la de Room
    suspend fun obtener(id: Long): Gasto? = try {
        api.gasto(id).aEntidad().also { gastoDao.guardar(it) }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        gastoDao.obtener(id)
    }

    // ---------- Técnico ----------
    // id = 0 → POST (crear); con id → PUT (actualizar). Devuelve el id que asignó el servidor.
    suspend fun guardar(gasto: Gasto): Long {
        val guardado = if (gasto.id == 0L) api.crearGasto(gasto.aRequest())
                       else api.actualizarGasto(gasto.id, gasto.aRequest())
        gastoDao.guardar(guardado.aEntidad())
        return guardado.id
    }

    suspend fun eliminar(gasto: Gasto) {
        api.eliminarGasto(gasto.id).exigirExito()
        gastoDao.eliminar(gasto)
    }

    // El duplicado se revisa contra TODOS los gastos del servidor (no solo los del teléfono).
    // Si el servidor no responde, se revisa al menos contra lo que hay en Room.
    suspend fun rendicionDelDuplicado(rut: String, folio: String, excluirId: Long = 0): String? = try {
        api.duplicado(rut, folio, excluirId).rendicion
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        gastoDao.rendicionDelDuplicado(rut, folio, excluirId)
    }

    suspend fun acumuladoDelDia(usuarioId: Long, categoriaId: Long, fecha: LocalDate, excluirId: Long = 0): Int = try {
        api.acumulado(usuarioId, categoriaId, fecha.toString(), excluirId).total.toInt()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        gastoDao.acumuladoDelDia(usuarioId, categoriaId, fecha, excluirId)
    }

    // ---------- Jefatura ----------
    // El servidor guarda la decisión y la anota en el historial
    suspend fun resolver(
        gasto: Gasto, nuevo: EstadoGasto, usuarioId: Long,
        montoAprobado: Int? = null, comentario: String? = null, motivo: MotivoRechazo? = null
    ) {
        val resuelto = api.resolver(gasto.id, ResolucionRequest(nuevo, usuarioId, montoAprobado, comentario, motivo))
        gastoDao.guardar(resuelto.aEntidad())
    }
}
