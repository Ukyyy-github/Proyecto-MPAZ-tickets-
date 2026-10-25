package com.escuela.mpazticket.repository

import com.escuela.mpazticket.data.local.dao.TicketDao
import com.escuela.mpazticket.data.local.dao.UserDao
import com.escuela.mpazticket.data.local.entities.TicketEntity
import com.escuela.mpazticket.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

class TicketRepositoryImpl(
    private val ticketDao: TicketDao,
    private val userDao: UserDao
) : TicketRepository {

    override fun getAllTickets(): Flow<List<TicketEntity>> = ticketDao.getAllTickets()

    override suspend fun getTicketById(id: Long): TicketEntity? = ticketDao.getTicketById(id)

    override suspend fun insertTicket(ticket: TicketEntity) = ticketDao.insertTicket(ticket)

    override suspend fun updateTicket(ticket: TicketEntity) = ticketDao.updateTicket(ticket)

    override suspend fun deleteTicket(id: Long) = ticketDao.deleteTicketById(id)

    override suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)

    override suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
}