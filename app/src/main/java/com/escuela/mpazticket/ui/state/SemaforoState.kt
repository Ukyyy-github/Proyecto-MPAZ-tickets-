package com.escuela.mpazticket.ui.state

import androidx.compose.ui.graphics.Color

enum class SemaforoUrgencia(val color: Color, val etiqueta: String) {
    VERDE(Color(0xFF4CAF50), "Normal"),
    AMARILLO(Color(0xFFFFC107), "Atención Requerida"),
    ROJO(Color(0xFFF44336), "Urgente / Crítico")
}

object SemaforoEvaluator {
    fun calcularUrgencia(fechaCreacionMillis: Long): SemaforoUrgencia {
        val diasTranscurridos = (System.currentTimeMillis() - fechaCreacionMillis) / (1000 * 60 * 60 * 24)
        return when {
            diasTranscurridos >= 10 -> SemaforoUrgencia.ROJO
            diasTranscurridos >= 4 -> SemaforoUrgencia.AMARILLO
            else -> SemaforoUrgencia.VERDE
        }
    }
}