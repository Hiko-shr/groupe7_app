package com.example.groupe7app.model.repository

import com.example.groupe7app.model.dao.UsuarioDao
import com.example.groupe7app.model.entity.Usuario
import com.example.groupe7app.model.remote.LoginRequest
import com.example.groupe7app.model.remote.UsuariosApi
import com.example.groupe7app.model.remote.aEntidad
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

// Ahora tiene DOS fuentes: el microservicio (ms-usuarios) y la copia local en Room.
// Los ViewModels no cambian: siguen pidiendo lo mismo y reciben los mismos Flow.
class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val api: UsuariosApi
) {

    // El login siempre lo valida el servidor. 401 = credenciales malas → null.
    // Sin conexión, la excepción sube al ViewModel para mostrar el mensaje.
    suspend fun autenticar(correo: String, clave: String): Usuario? = try {
        val usuario = api.login(LoginRequest(correo.trim().lowercase(), clave)).aEntidad()
        usuarioDao.guardar(usuario)                    // queda en Room para el saludo, el perfil, etc.
        usuario
    } catch (e: HttpException) {
        if (e.code() == 401) null else throw e
    }

    fun obtener(id: Long): Flow<Usuario?> =
        usuarioDao.obtener(id).refrescandoCon { usuarioDao.guardar(api.obtener(id).aEntidad()) }

    fun deEquipo(jefaturaId: Long): Flow<List<Usuario>> =
        usuarioDao.deEquipo(jefaturaId).refrescandoCon {
            usuarioDao.guardarTodos(api.listar(jefaturaId).map { it.aEntidad() })
        }

    fun todos(): Flow<List<Usuario>> =
        usuarioDao.todos().refrescandoCon { usuarioDao.guardarTodos(api.listar().map { it.aEntidad() }) }
}
