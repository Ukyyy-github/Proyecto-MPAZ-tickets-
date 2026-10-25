package com.escuela.mpazticket.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.escuela.mpazticket.data.local.entities.TicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {

    @Query("SELECT * FROM tickets ORDER BY fechaCreacionTimestamp DESC")
    fun getAllTickets(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE id = :ticketId")
    suspend fun getTicketById(ticketId: Long): TicketEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity)

    @Update
    suspend fun updateTicket(ticket: TicketEntity)

    @Query("DELETE FROM tickets WHERE id = :ticketId")
    suspend fun deleteTicketById(ticketId: Long)
}