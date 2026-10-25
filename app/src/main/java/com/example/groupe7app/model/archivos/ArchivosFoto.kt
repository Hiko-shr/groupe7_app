package com.example.groupe7app.model.archivos

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

// Fotos de comprobantes: se guardan en la memoria interna de la app (filesDir/comprobantes),
// así no se borran como pasaría con la caché.
object ArchivosFoto {

    private fun carpeta(context: Context): File =
        File(context.filesDir, "comprobantes").apply { mkdirs() }

    // Archivo vacío donde la cámara escribirá la foto
    fun nuevoArchivo(context: Context): File =
        File(carpeta(context), "comprobante_${System.currentTimeMillis()}.jpg")

    // La cámara es otra app: necesita una Uri "content://" que le dé permiso para escribir el archivo
    fun uriPara(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    // La imagen elegida de la galería se copia a nuestra carpeta (la original podría borrarse)
    fun copiarDesdeGaleria(context: Context, origen: Uri): String? {
        val destino = nuevoArchivo(context)
        val entrada = context.contentResolver.openInputStream(origen) ?: return null
        entrada.use { input -> destino.outputStream().use { output -> input.copyTo(output) } }
        return destino.absolutePath
    }
}
