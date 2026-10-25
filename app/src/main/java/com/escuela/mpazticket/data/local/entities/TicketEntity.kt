package com.escuela.mpazticket.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val categoria: String,        // Infraestructura, Mobiliario, Tecnología
    val ubicacion: String,        // Cancha, Biblioteca, Sala PIE, etc.
    val prioridadInicial: Priority,
    val estado: TicketStatus = TicketStatus.PENDIENTE,
    val fotoPath: String? = null, // Ruta de la foto local (opcional)
    val creadoPorEmail: String,
    val asignadoAEmail: String? = null,
    val fechaCreacionTimestamp: Long = System.currentTimeMillis()
) {
    /**
     * Calcula el color del semáforo considerando la gravedad inicial
     * y los días transcurridos respecto al límite de 2 semanas (14 días).
     */
    fun obtenerColorSemaforo(): TrafficLightColor {
        val diasTranscurridos = TimeUnit.MILLISECONDS.toDays(
            System.currentTimeMillis() - fechaCreacionTimestamp
        )

        return when {
            // Regla 1: Urgente por gravedad ALTA o más de 10 días esperando
            prioridadInicial == Priority.ALTA || diasTranscurridos >= 10 -> TrafficLightColor.ROJO

            // Regla 2: Advertencia por gravedad MEDIA o más de 4 días esperando
            prioridadInicial == Priority.MEDIA || diasTranscurridos >= 4 -> TrafficLightColor.AMARILLO

            // Regla 3: Recién creado y de baja gravedad
            else -> TrafficLightColor.VERDE
        }
    }
}