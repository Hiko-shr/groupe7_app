package com.example.groupe7app.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.enums.TipoRendicion
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "rendiciones",
    foreignKeys = [
        ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["usuarioId"]),
        ForeignKey(entity = CentroCosto::class, parentColumns = ["codigo"], childColumns = ["centroCosto"]),
        ForeignKey(entity = Pago::class, parentColumns = ["id"], childColumns = ["pagoId"],
            onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("usuarioId"), Index("centroCosto"), Index("pagoId")]
)
data class Rendicion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val numero: String,                       // "RND-2026-000214"
    val usuarioId: Long,                      // el técnico dueño (faltaba en la versión anterior)
    val titulo: String,
    val tipo: TipoRendicion,
    val destino: String? = null,              // solo si es comisión de servicio
    val desde: LocalDate,
    val hasta: LocalDate,
    val centroCosto: String,                  // código de CentroCosto
    val observacion: String? = null,
    val estado: EstadoRendicion = EstadoRendicion.BORRADOR,
    val fechaEnvio: LocalDateTime? = null,    // se llena al enviar a aprobación
    val pagoId: Long? = null                  // se llena cuando Finanzas paga
)