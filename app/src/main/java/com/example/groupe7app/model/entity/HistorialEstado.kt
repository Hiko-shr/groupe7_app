package com.example.groupe7app.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "historial_estados",
    indices = [Index("rendicionId")]
)
data class HistorialEstado(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rendicionId: Long,
    val gastoId: Long? = null,          // null si el cambio fue de la rendición completa
    val usuarioId: Long,                // quién hizo el cambio
    val estadoAnterior: String?,        // null al crear
    val estadoNuevo: String,
    val comentario: String? = null,
    val fecha: LocalDateTime
)