package com.example.groupe7app.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.groupe7app.model.enums.EstadoGasto
import com.example.groupe7app.model.enums.MedioPago
import com.example.groupe7app.model.enums.MotivoRechazo
import com.example.groupe7app.model.enums.TipoDocumento
import java.time.LocalDate

// Copia local de ms-rendiciones. Las reglas de integridad (rendición existente, categoría existente,
// RUT + folio único) ahora las aplica MySQL en el servidor.
@Entity(
    tableName = "gastos",
    indices = [Index("rendicionId"), Index("categoriaId")]
)
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rendicionId: Long,
    val categoriaId: Long,
    val fechaDocumento: LocalDate,
    val tipoDocumento: TipoDocumento,
    val rutEmisor: String? = null,            // null en tickets sin RUT
    val folio: String? = null,
    val proveedor: String,
    val monto: Int,                           // lo que dice la boleta
    val montoAdmitido: Int,                   // lo que respeta el tope
    val justificacion: String? = null,        // si superó el tope y la categoría lo permite
    val medioPago: MedioPago,
    val centroCosto: String,
    val descripcion: String,
    val ordenTrabajo: String? = null,         // campo opcional del mockup
    val rutaFoto: String,                     // archivo de la foto en el teléfono
    val latitud: Double? = null,              // null si no hubo permiso de ubicación
    val longitud: Double? = null,
    val estado: EstadoGasto = EstadoGasto.BORRADOR,
    val montoAprobado: Int? = null,           // lo llena la jefatura (puede reducirlo)
    val comentarioRevision: String? = null,   // comentario de la jefatura al observar o rechazar
    val motivoRechazo: MotivoRechazo? = null,
    val respuestaTecnico: String? = null      // respuesta del técnico a una observación
)