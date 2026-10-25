package com.example.groupe7app.model.enums

enum class Rol { TECNICO, JEFATURA, FINANZAS }
enum class EstadoRendicion { BORRADOR, ENVIADA, OBSERVADA, APROBADA_JEFATURA, APROBADA_FINANZAS, PAGADA, RECHAZADA }
enum class EstadoGasto { BORRADOR, ENVIADO, OBSERVADO, APROBADO, APROBADO_FINAL, RECHAZADO }
enum class MedioPago(val etiqueta: String) { FONDO_POR_RENDIR("Fondo por rendir"), TARJETA_CORPORATIVA("Tarjeta corporativa"), DINERO_PROPIO("Dinero propio") }
enum class TipoDocumento(val etiqueta: String) { BOLETA("Boleta"), FACTURA("Factura"), TICKET("Ticket") }
enum class TipoRendicion(val etiqueta: String) { GASTOS_GENERALES("Gastos generales"), COMISION_SERVICIO("Comisión de servicio") }
enum class MotivoRechazo(val etiqueta: String) { COMPROBANTE_ILEGIBLE("Comprobante ilegible"), NO_RELACIONADO("No relacionado"), FUERA_DE_POLITICA("Fuera de política"), OTRO("Otro") }