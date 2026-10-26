package com.escuela.mpazticket.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.escuela.mpazticket.ui.state.SemaforoEvaluator
import com.escuela.mpazticket.ui.state.SemaforoUrgencia

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
     * y los días transcurridos mediante SemaforoEvaluator.
     */
    fun obtenerColorSemaforo(): TrafficLightColor {
        val urgencia = SemaforoEvaluator.calcularUrgencia(fechaCreacionTimestamp, prioridadInicial)
        return when (urgencia) {
            SemaforoUrgencia.VERDE -> TrafficLightColor.VERDE
            SemaforoUrgencia.AMARILLO -> TrafficLightColor.AMARILLO
            SemaforoUrgencia.ROJO -> TrafficLightColor.ROJO
        }
    }
}