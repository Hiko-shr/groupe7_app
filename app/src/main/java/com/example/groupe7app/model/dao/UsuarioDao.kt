package com.example.groupe7app.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.groupe7app.model.entity.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    // Login: buscar por correo (después se compara la clave)
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): Usuario?

    // Perfil y saludo "Hola, Matías"
    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun obtener(id: Long): Flow<Usuario?>

    // Jefatura: los técnicos de su equipo (para mostrar sus nombres en la bandeja)
    @Query("SELECT * FROM usuarios WHERE jefaturaId = :jefaturaId")
    fun deEquipo(jefaturaId: Long): Flow<List<Usuario>>

    // Para el DataSeeder: saber si la base está vacía
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Insert
    suspend fun insertarTodos(usuarios: List<Usuario>)
}