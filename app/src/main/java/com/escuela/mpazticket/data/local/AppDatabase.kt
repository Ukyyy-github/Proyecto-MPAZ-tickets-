package com.escuela.mpazticket.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.escuela.mpazticket.data.local.dao.TicketDao
import com.escuela.mpazticket.data.local.dao.UserDao
import com.escuela.mpazticket.data.local.entities.DocumentEntity
import com.escuela.mpazticket.data.local.entities.TicketEntity
import com.escuela.mpazticket.data.local.entities.UserEntity

@Database(
    entities = [TicketEntity::class, UserEntity::class, DocumentEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ticketDao(): TicketDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpaz_ticket_database"
                )
                    .fallbackToDestructiveMigration() // Evita crashes por inconsistencias de versiones de Room
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}