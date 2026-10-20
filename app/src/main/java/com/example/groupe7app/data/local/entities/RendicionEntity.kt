package com.example.groupe7app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rendiciones")
data class RendicionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codigo: String,         // Ej: RND-2026-000214
    val titulo: String,         // Ej: Comisión Los Ángeles · autoclaves
    val tipo: String,           // General / Comisión
    val destino: String? = null,
    val fechaDesde: String,     // DD-MM-YYYY
    val fechaHasta: String,     // DD-MM-YYYY
    val centroCosto: String,    // CC-SERV-CONCE
    val observacion: String? = null,
    val estado: String = "BORRADOR" // BORRADOR, ENVIADA, OBSERVADA, PAGADA
)