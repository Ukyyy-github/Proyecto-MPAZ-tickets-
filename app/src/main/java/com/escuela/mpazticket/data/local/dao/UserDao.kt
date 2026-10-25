package com.escuela.mpazticket.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.escuela.mpazticket.data.local.entities.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
}