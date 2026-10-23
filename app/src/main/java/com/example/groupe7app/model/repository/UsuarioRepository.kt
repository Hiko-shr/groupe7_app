package com.example.groupe7app.model.repository

import com.example.groupe7app.model.dao.UsuarioDao
import com.example.groupe7app.model.entity.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    // Devuelve el usuario si correo y clave coinciden; null si no
    suspend fun autenticar(correo: String, clave: String): Usuario? {
        val usuario = usuarioDao.buscarPorCorreo(correo.trim().lowercase())
        return if (usuario != null && usuario.clave == clave) usuario else null
    }

    fun obtener(id: Long): Flow<Usuario?> = usuarioDao.obtener(id)
}