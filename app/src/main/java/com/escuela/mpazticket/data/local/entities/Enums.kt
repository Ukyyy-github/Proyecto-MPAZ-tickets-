package com.escuela.mpazticket.data.local.entities

// Gravedad inicial elegida al reportar
enum class Priority {
    BAJA, MEDIA, ALTA
}

// Colores del semáforo automático
enum class TrafficLightColor {
    VERDE, AMARILLO, ROJO
}

// Ciclo de vida del ticket
enum class TicketStatus {
    PENDIENTE, REVISADO, ASIGNADO, EN_PROCESO, SOLUCIONADO, CERRADO
}

// Roles de usuario
enum class UserRole {
    SOLICITANTE, ENCARGADO_TI, AUXILIAR
}