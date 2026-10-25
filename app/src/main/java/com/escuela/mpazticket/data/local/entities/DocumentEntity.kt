package com.escuela.mpazticket.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documentos")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreArchivo: String,
    val uriPath: String,        // Ruta local del PDF o Word
    val tipoExtension: String, // "pdf", "doc", "docx"
    val fechaSubidaTimestamp: Long = System.currentTimeMillis()
)