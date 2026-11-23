package com.example.groupe7app.model.remote

import com.example.groupe7app.model.enums.EstadoRendicion
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Endpoints de ms-rendiciones (puerto 8082): los mismos que probaste en Postman
interface RendicionesApi {

    // ---------- Catálogos ----------
    @GET("api/categorias")
    suspend fun categorias(): List<CategoriaDto>

    @GET("api/centros-costo")
    suspend fun centrosCosto(): List<CentroCostoDto>

    // ---------- Rendiciones ----------
    // usuarioIds se envía como "1,2,3" (ver RemoteMappers.comoLista)
    @GET("api/rendiciones")
    suspend fun rendiciones(
        @Query("usuarioId") usuarioId: Long? = null,
        @Query("estado") estado: EstadoRendicion? = null,
        @Query("usuarioIds") usuarioIds: String? = null
    ): List<RendicionDto>

    @GET("api/rendiciones/{id}")
    suspend fun rendicion(@Path("id") id: Long): RendicionDto

    @POST("api/rendiciones")
    suspend fun crearRendicion(@Body body: RendicionRequest): RendicionDto

    @PUT("api/rendiciones/{id}")
    suspend fun actualizarRendicion(@Path("id") id: Long, @Body body: RendicionRequest): RendicionDto

    @DELETE("api/rendiciones/{id}")
    suspend fun eliminarRendicion(@Path("id") id: Long): Response<Unit>     // 204 sin cuerpo

    @POST("api/rendiciones/{id}/enviar")
    suspend fun enviar(@Path("id") id: Long, @Body body: AccionRequest): RendicionDto

    @POST("api/rendiciones/{id}/estado")
    suspend fun cambiarEstado(@Path("id") id: Long, @Body body: CambioEstadoRequest): RendicionDto

    @POST("api/rendiciones/{id}/aprobar-finanzas")
    suspend fun aprobarFinanzas(@Path("id") id: Long, @Body body: AccionRequest): RendicionDto

    @GET("api/rendiciones/{id}/historial")
    suspend fun historial(@Path("id") id: Long): List<HistorialDto>

    // ---------- Gastos ----------
    @GET("api/gastos")
    suspend fun gastos(
        @Query("rendicionId") rendicionId: Long? = null,
        @Query("usuarioId") usuarioId: Long? = null,
        @Query("usuarioIds") usuarioIds: String? = null,
        @Query("estadoRendicion") estadoRendicion: EstadoRendicion? = null
    ): List<GastoDto>

    @GET("api/gastos/{id}")
    suspend fun gasto(@Path("id") id: Long): GastoDto

    @POST("api/gastos")
    suspend fun crearGasto(@Body body: GastoRequest): GastoDto

    @PUT("api/gastos/{id}")
    suspend fun actualizarGasto(@Path("id") id: Long, @Body body: GastoRequest): GastoDto

    @DELETE("api/gastos/{id}")
    suspend fun eliminarGasto(@Path("id") id: Long): Response<Unit>

    @POST("api/gastos/{id}/resolver")
    suspend fun resolver(@Path("id") id: Long, @Body body: ResolucionRequest): GastoDto

    @GET("api/gastos/duplicado")
    suspend fun duplicado(
        @Query("rut") rut: String,
        @Query("folio") folio: String,
        @Query("excluirId") excluirId: Long
    ): DuplicadoDto

    @GET("api/gastos/acumulado")
    suspend fun acumulado(
        @Query("usuarioId") usuarioId: Long,
        @Query("categoriaId") categoriaId: Long,
        @Query("fecha") fecha: String,
        @Query("excluirId") excluirId: Long
    ): AcumuladoDto

    // ---------- Pagos ----------
    @POST("api/pagos")
    suspend fun registrarPago(@Body body: PagoRequest): PagoDto
}
