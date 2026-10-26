package com.escuela.mpazticket.ui.state

import androidx.compose.ui.graphics.Color
import com.escuela.mpazticket.data.local.entities.Priority

enum class SemaforoUrgencia(val color: Color, val etiqueta: String) {
    VERDE(Color(0xFF4CAF50), "Normal"),
    AMARILLO(Color(0xFFFFC107), "Atención Requerida"),
    ROJO(Color(0xFFF44336), "Urgente / Crítico")
}

object SemaforoEvaluator {
    fun calcularUrgencia(fechaCreacionMillis: Long, prioridad: Priority = Priority.MEDIA): SemaforoUrgencia {
        val diasTranscurridos = (System.currentTimeMillis() - fechaCreacionMillis) / (1000 * 60 * 60 * 24)
        return when (prioridad) {
            Priority.BAJA -> {
                when {
                    diasTranscurridos >= 10 -> SemaforoUrgencia.ROJO
                    diasTranscurridos >= 4 -> SemaforoUrgencia.AMARILLO
                    else -> SemaforoUrgencia.VERDE
                }
            }
            Priority.MEDIA -> {
                when {
                    diasTranscurridos >= 7 -> SemaforoUrgencia.ROJO
                    diasTranscurridos >= 2 -> SemaforoUrgencia.AMARILLO
                    else -> SemaforoUrgencia.VERDE
                }
            }
            Priority.ALTA -> {
                when {
                    diasTranscurridos >= 3 -> SemaforoUrgencia.ROJO
                    else -> SemaforoUrgencia.AMARILLO
                }
            }
        }
    }
}