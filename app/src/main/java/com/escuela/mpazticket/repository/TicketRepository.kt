package com.escuela.mpazticket.repository

import com.escuela.mpazticket.data.local.entities.TicketEntity
import com.escuela.mpazticket.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    fun getAllTickets(): Flow<List<TicketEntity>>
    suspend fun getTicketById(id: Long): TicketEntity?
    suspend fun insertTicket(ticket: TicketEntity)
    suspend fun updateTicket(ticket: TicketEntity)
    suspend fun deleteTicket(id: Long)

    suspend fun getUserByEmail(email: String): UserEntity?
    suspend fun insertUser(user: UserEntity)
}
