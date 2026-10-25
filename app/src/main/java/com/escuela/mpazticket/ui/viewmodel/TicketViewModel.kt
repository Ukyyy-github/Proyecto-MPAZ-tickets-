package com.escuela.mpazticket.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escuela.mpazticket.data.local.entities.Priority
import com.escuela.mpazticket.data.local.entities.TicketEntity
import com.escuela.mpazticket.data.local.entities.TicketStatus
import com.escuela.mpazticket.repository.TicketRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TicketViewModel(
    private val repository: TicketRepository
) : ViewModel() {

    val ticketsList: StateFlow<List<TicketEntity>> = repository.getAllTickets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun registrarTicket(
        titulo: String,
        descripcion: String,
        ubicacion: String,
        categoria: String,
        prioridadInicial: Priority,
        creadoPorEmail: String
    ) {
        viewModelScope.launch {
            val nuevoTicket = TicketEntity(
                titulo = titulo,
                descripcion = descripcion,
                ubicacion = ubicacion,
                categoria = categoria,
                prioridadInicial = prioridadInicial,
                estado = TicketStatus.PENDIENTE,
                creadoPorEmail = creadoPorEmail
            )
            repository.insertTicket(nuevoTicket)
        }
    }

    fun actualizarEstadoTicket(ticket: TicketEntity, nuevoEstado: TicketStatus) {
        viewModelScope.launch {
            val ticketActualizado = ticket.copy(estado = nuevoEstado)
            repository.updateTicket(ticketActualizado)
        }
    }

    fun eliminarTicket(ticketId: Long) {
        viewModelScope.launch {
            repository.deleteTicket(ticketId)
        }
    }
}