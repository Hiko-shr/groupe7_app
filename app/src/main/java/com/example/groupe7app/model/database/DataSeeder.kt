package com.example.groupe7app.model.database

import androidx.room.withTransaction
import com.example.groupe7app.model.entity.Categoria
import com.example.groupe7app.model.entity.CentroCosto
import com.example.groupe7app.model.entity.Gasto
import com.example.groupe7app.model.entity.Rendicion
import com.example.groupe7app.model.entity.Usuario
import com.example.groupe7app.model.enums.EstadoGasto
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.enums.MedioPago
import com.example.groupe7app.model.enums.Rol
import com.example.groupe7app.model.enums.TipoDocumento
import com.example.groupe7app.model.enums.TipoRendicion
import java.time.LocalDate
import java.time.LocalDateTime

class DataSeeder(private val db: AppDatabase) {

    suspend fun poblarSiEstaVacia() {
        if (db.usuarioDao().contar() > 0) return      // ya tiene datos: no hacer nada

        // withTransaction: o se guarda TODO o NADA (si algo falla a la mitad, no queda a medias)
        db.withTransaction {
            insertarCatalogos()
            insertarRendicionesDePrueba()
        }
    }

    private suspend fun insertarCatalogos() {
        db.centroCostoDao().insertarTodos(listOf(
            CentroCosto(CONCE, "Servicio técnico Concepción"),
            CentroCosto(SUR, "Servicio técnico zona sur")
        ))

        // Topes del Apéndice C del SRS
        db.categoriaDao().insertarTodas(listOf(
            Categoria(id = 1, nombre = "Combustible", topePorGasto = 80_000, permiteExceso = true),
            Categoria(id = 2, nombre = "Peaje", topePorGasto = 10_000, permiteExceso = true),
            Categoria(id = 3, nombre = "Estacionamiento", topeDiario = 10_000, permiteExceso = false),
            Categoria(id = 4, nombre = "Alimentación", topeDiario = 15_000, permiteExceso = false),
            Categoria(id = 5, nombre = "Alojamiento", topePorGasto = 60_000, permiteExceso = true),
            Categoria(id = 6, nombre = "Insumos urgentes", topePorGasto = 100_000, permiteExceso = true),
            Categoria(id = 7, nombre = "Transporte", topeDiario = 20_000, permiteExceso = true),
            Categoria(id = 8, nombre = "Otros", topePorGasto = 20_000, permiteExceso = true)
        ))

        // Contraseña de todos: Teq12345. Los técnicos (1 a 4) dependen de Paula (id 5).
        db.usuarioDao().insertarTodos(listOf(
            Usuario(id = 1, nombre = "Matías Fuentes", correo = "tecnico@teqmed.cl", clave = CLAVE, rol = Rol.TECNICO, jefaturaId = 5, tieneTarjeta = true),
            Usuario(id = 2, nombre = "Javiera Muñoz", correo = "javiera@teqmed.cl", clave = CLAVE, rol = Rol.TECNICO, jefaturaId = 5),
            Usuario(id = 3, nombre = "Diego Paredes", correo = "diego@teqmed.cl", clave = CLAVE, rol = Rol.TECNICO, jefaturaId = 5, tieneTarjeta = true),
            Usuario(id = 4, nombre = "Ignacio Toro", correo = "ignacio@teqmed.cl", clave = CLAVE, rol = Rol.TECNICO, jefaturaId = 5),
            Usuario(id = 5, nombre = "Paula Riquelme", correo = "jefatura@teqmed.cl", clave = CLAVE, rol = Rol.JEFATURA),
            Usuario(id = 6, nombre = "Andrés Vidal", correo = "finanzas@teqmed.cl", clave = CLAVE, rol = Rol.FINANZAS)
        ))
    }

    private suspend fun insertarRendicionesDePrueba() {
        val enviada = LocalDateTime.of(2026, 10, 15, 9, 0)

        listOf(
            // Matías: una en borrador y una observada (para el perfil Técnico)
            Rendicion(id = 1, numero = "RND-2026-000001", usuarioId = 1, titulo = "Comisión Los Ángeles · autoclaves",
                tipo = TipoRendicion.COMISION_SERVICIO, destino = "Los Ángeles",
                desde = fecha(10, 1), hasta = fecha(10, 5), centroCosto = CONCE),
            Rendicion(id = 2, numero = "RND-2026-000002", usuarioId = 1, titulo = "Mantención Hospital Regional",
                tipo = TipoRendicion.GASTOS_GENERALES, desde = fecha(10, 6), hasta = fecha(10, 10),
                centroCosto = CONCE, estado = EstadoRendicion.OBSERVADA, fechaEnvio = enviada),
            // Javiera y Diego: enviadas (para el perfil Jefatura)
            Rendicion(id = 3, numero = "RND-2026-000003", usuarioId = 2, titulo = "Visita Clínica Sanatorio Alemán",
                tipo = TipoRendicion.GASTOS_GENERALES, desde = fecha(10, 12), hasta = fecha(10, 14),
                centroCosto = CONCE, estado = EstadoRendicion.ENVIADA, fechaEnvio = enviada),
            Rendicion(id = 4, numero = "RND-2026-000004", usuarioId = 3, titulo = "Instalación rayos X Chillán",
                tipo = TipoRendicion.COMISION_SERVICIO, destino = "Chillán", desde = fecha(10, 13), hasta = fecha(10, 16),
                centroCosto = SUR, estado = EstadoRendicion.ENVIADA, fechaEnvio = enviada.plusDays(2)),
            // Ignacio y Diego: aprobadas (para el perfil Finanzas)
            Rendicion(id = 5, numero = "RND-2026-000005", usuarioId = 4, titulo = "Mantención preventiva Clínica Biobío",
                tipo = TipoRendicion.GASTOS_GENERALES, desde = fecha(10, 5), hasta = fecha(10, 8),
                centroCosto = CONCE, estado = EstadoRendicion.APROBADA_JEFATURA, fechaEnvio = enviada),
            Rendicion(id = 6, numero = "RND-2026-000006", usuarioId = 3, titulo = "Soporte equipos Los Ángeles",
                tipo = TipoRendicion.GASTOS_GENERALES, desde = fecha(9, 28), hasta = fecha(9, 30),
                centroCosto = SUR, estado = EstadoRendicion.APROBADA_FINANZAS, fechaEnvio = enviada)
        ).forEach { db.rendicionDao().insertar(it) }

        db.gastoDao().insertarTodos(listOf(
            // Rendición 1 (borrador de Matías)
            gasto(1, 1, fecha(10, 1), "Estación de servicio Ruta 5", 21_450, MedioPago.TARJETA_CORPORATIVA, EstadoGasto.BORRADOR,
                rut = "76543210-3", folio = "184001"),
            gasto(1, 4, fecha(10, 3), "Restaurante El Alero", 17_800, MedioPago.DINERO_PROPIO, EstadoGasto.BORRADOR,
                rut = "96511460-2", folio = "184392", admitido = 15_000),
            gasto(1, 5, fecha(10, 3), "Hostal Laja Centro", 64_000, MedioPago.DINERO_PROPIO, EstadoGasto.BORRADOR,
                rut = "77261280-K", folio = "5521", justificacion = "Único alojamiento disponible cerca del hospital"),
            // Rendición 2 (observada)
            gasto(2, 3, fecha(10, 7), "Estacionamiento hospital", 3_500, MedioPago.DINERO_PROPIO, EstadoGasto.OBSERVADO,
                comentario = "La foto del ticket no se lee, vuelve a fotografiarlo"),
            // Rendición 3 (enviada, con un gasto que supera el tope)
            gasto(3, 1, fecha(10, 12), "Copec Concepción", 92_000, MedioPago.DINERO_PROPIO, EstadoGasto.ENVIADO,
                rut = "78090450-K", folio = "3301", justificacion = "Viaje de ida y vuelta con equipo en la camioneta"),
            gasto(3, 2, fecha(10, 12), "Autopista del Itata", 2_300, MedioPago.FONDO_POR_RENDIR, EstadoGasto.ENVIADO),
            // Rendición 4 (enviada)
            gasto(4, 5, fecha(10, 13), "Hotel Isabel Riquelme", 55_000, MedioPago.DINERO_PROPIO, EstadoGasto.ENVIADO,
                rut = "76123456-0", folio = "8812", centroCosto = SUR),
            gasto(4, 4, fecha(10, 14), "Café Ñuble", 12_000, MedioPago.DINERO_PROPIO, EstadoGasto.ENVIADO,
                rut = "99520000-7", folio = "4410", centroCosto = SUR),
            // Rendición 5 (aprobada por jefatura)
            gasto(5, 1, fecha(10, 5), "Shell Talcahuano", 40_000, MedioPago.DINERO_PROPIO, EstadoGasto.APROBADO,
                rut = "76987654-5", folio = "7001", aprobado = 40_000),
            gasto(5, 6, fecha(10, 6), "Insumos Médicos Biobío", 35_000, MedioPago.FONDO_POR_RENDIR, EstadoGasto.APROBADO,
                rut = "96804330-7", folio = "7002", aprobado = 35_000),
            // Rendición 6 (aprobada por finanzas, lista para pagar)
            gasto(6, 7, fecha(9, 29), "Taxi aeropuerto", 15_000, MedioPago.DINERO_PROPIO, EstadoGasto.APROBADO_FINAL,
                aprobado = 15_000, centroCosto = SUR)
        ))
    }

    // Función de ayuda: arma un Gasto con valores por defecto para no repetir 20 campos cada vez
    private fun gasto(
        rendicionId: Long, categoriaId: Long, fechaDoc: LocalDate, proveedor: String, monto: Int,
        medioPago: MedioPago, estado: EstadoGasto,
        rut: String? = null, folio: String? = null,
        admitido: Int = monto, aprobado: Int? = null,
        justificacion: String? = null, comentario: String? = null,
        centroCosto: String = CONCE
    ) = Gasto(
        rendicionId = rendicionId, categoriaId = categoriaId, fechaDocumento = fechaDoc,
        tipoDocumento = if (rut == null) TipoDocumento.TICKET else TipoDocumento.BOLETA,
        rutEmisor = rut, folio = folio, proveedor = proveedor,
        monto = monto, montoAdmitido = admitido, montoAprobado = aprobado,
        justificacion = justificacion, medioPago = medioPago, centroCosto = centroCosto,
        descripcion = "Gasto de prueba", rutaFoto = "", estado = estado,
        comentarioRevision = comentario
    )

    private fun fecha(mes: Int, dia: Int): LocalDate = LocalDate.of(2026, mes, dia)

    companion object {
        private const val CLAVE = "Teq12345"
        private const val CONCE = "CC-SERV-CONCE"
        private const val SUR = "CC-SERV-SUR"
    }
}