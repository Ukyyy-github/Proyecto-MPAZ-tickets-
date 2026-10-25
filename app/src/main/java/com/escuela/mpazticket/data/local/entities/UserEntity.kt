package com.escuela.mpazticket.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UserEntity(
    @PrimaryKey
    val email: String, // Identificador único (sin RUT)
    val nombreCompleto: String,
    val contrasena: String,
    val rol: UserRole
)