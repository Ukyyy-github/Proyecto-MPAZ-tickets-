package com.escuela.mpazticket.repository

import android.content.Context
import com.escuela.mpazticket.data.local.entities.Priority
import com.escuela.mpazticket.data.local.entities.TicketEntity
import com.escuela.mpazticket.data.local.entities.TicketStatus
import com.escuela.mpazticket.data.local.entities.UserEntity
import com.escuela.mpazticket.data.local.entities.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class LocalTicketRepository(context: Context) : TicketRepository {

    private val ticketsFile = File(context.filesDir, "tickets.json")
    private val usersFile = File(context.filesDir, "users.json")

    private val _ticketsFlow = MutableStateFlow<List<TicketEntity>>(loadInitialTickets())
    private val usersMap = mutableMapOf<String, UserEntity>()

    init {
        loadInitialUsers()
    }

    private fun loadInitialTickets(): List<TicketEntity> {
        return try {
            val list = (if (ticketsFile.exists()) {
                val jsonString = ticketsFile.readText()
                val jsonArray = JSONArray(jsonString)
                val mutableList = mutableListOf<TicketEntity>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    mutableList.add(
                        TicketEntity(
                            id = obj.getLong("id"),
                            titulo = obj.getString("titulo"),
                            descripcion = obj.getString("descripcion"),
                            categoria = obj.getString("categoria"),
                            ubicacion = obj.getString("ubicacion"),
                            prioridadInicial = Priority.valueOf(obj.getString("prioridadInicial")),
                            estado = TicketStatus.valueOf(obj.getString("estado")),
                            fotoPath = if (obj.has("fotoPath") && !obj.isNull("fotoPath")) obj.getString("fotoPath") else null,
                            creadoPorEmail = obj.getString("creadoPorEmail"),
                            asignadoAEmail = if (obj.has("asignadoAEmail") && !obj.isNull("asignadoAEmail")) obj.getString("asignadoAEmail") else null,
                            fechaCreacionTimestamp = obj.getLong("fechaCreacionTimestamp")
                        )
                    )
                }
                if (mutableList.isEmpty()) getDefaultTickets() else mutableList
            } else {
                getDefaultTickets()
            }).toMutableList()

            if (cleanOldPhotos(list)) {
                saveTicketsToFile(list)
            }
            list
        } catch (e: Exception) {
            getDefaultTickets()
        }
    }

    private fun cleanOldPhotos(list: MutableList<TicketEntity>): Boolean {
        val twentyEightDaysMillis = 28L * 24 * 60 * 60 * 1000L
        val now = System.currentTimeMillis()
        var modified = false
        for (i in list.indices) {
            val t = list[i]
            if (!t.fotoPath.isNullOrBlank() && (now - t.fechaCreacionTimestamp) > twentyEightDaysMillis) {
                try {
                    val file = File(t.fotoPath)
                    if (file.exists()) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                list[i] = t.copy(fotoPath = null)
                modified = true
            }
        }
        return modified
    }

    private fun getDefaultTickets(): List<TicketEntity> {
        val defaultList = listOf(
            TicketEntity(
                id = 1L,
                titulo = "Proyector dañado en Sala 3",
                descripcion = "El proyector no enciende al conectar el cable HDMI.",
                categoria = "Tecnología",
                ubicacion = "Sala 3",
                prioridadInicial = Priority.ALTA,
                estado = TicketStatus.PENDIENTE,
                creadoPorEmail = "profesor@mpazticket.com",
                fechaCreacionTimestamp = System.currentTimeMillis() - 86400000L * 2
            ),
            TicketEntity(
                id = 2L,
                titulo = "Silla rota en Biblioteca",
                descripcion = "Una de las sillas de estudio tiene una pata suelta.",
                categoria = "Mobiliario",
                ubicacion = "Biblioteca",
                prioridadInicial = Priority.MEDIA,
                estado = TicketStatus.EN_PROCESO,
                creadoPorEmail = "estudiante@mpazticket.com",
                fechaCreacionTimestamp = System.currentTimeMillis() - 86400000L * 5
            )
        )
        saveTicketsToFile(defaultList)
        return defaultList
    }

    private fun saveTicketsToFile(tickets: List<TicketEntity>) {
        try {
            val jsonArray = JSONArray()
            for (t in tickets) {
                val obj = JSONObject().apply {
                    put("id", t.id)
                    put("titulo", t.titulo)
                    put("descripcion", t.descripcion)
                    put("categoria", t.categoria)
                    put("ubicacion", t.ubicacion)
                    put("prioridadInicial", t.prioridadInicial.name)
                    put("estado", t.estado.name)
                    put("fotoPath", t.fotoPath ?: JSONObject.NULL)
                    put("creadoPorEmail", t.creadoPorEmail)
                    put("asignadoAEmail", t.asignadoAEmail ?: JSONObject.NULL)
                    put("fechaCreacionTimestamp", t.fechaCreacionTimestamp)
                }
                jsonArray.put(obj)
            }
            ticketsFile.writeText(jsonArray.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadInitialUsers() {
        try {
            if (usersFile.exists()) {
                val jsonString = usersFile.readText()
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val user = UserEntity(
                        email = obj.getString("email"),
                        nombreCompleto = obj.getString("nombreCompleto"),
                        contrasena = obj.getString("contrasena"),
                        rol = UserRole.valueOf(obj.getString("rol"))
                    )
                    usersMap[user.email] = user
                }
            }
            if (usersMap.isEmpty()) {
                val defaultUser = UserEntity(
                    email = "admin@mpazticket.com",
                    nombreCompleto = "Administrador",
                    contrasena = "123456",
                    rol = UserRole.ENCARGADO_TI
                )
                usersMap[defaultUser.email] = defaultUser
                saveUsersToFile()
            }
        } catch (e: Exception) {
            val defaultUser = UserEntity(
                email = "admin@mpazticket.com",
                nombreCompleto = "Administrador",
                contrasena = "123456",
                rol = UserRole.ENCARGADO_TI
            )
            usersMap[defaultUser.email] = defaultUser
        }
    }

    private fun saveUsersToFile() {
        try {
            val jsonArray = JSONArray()
            for (user in usersMap.values) {
                val obj = JSONObject().apply {
                    put("email", user.email)
                    put("nombreCompleto", user.nombreCompleto)
                    put("contrasena", user.contrasena)
                    put("rol", user.rol.name)
                }
                jsonArray.put(obj)
            }
            usersFile.writeText(jsonArray.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAllTickets(): Flow<List<TicketEntity>> = _ticketsFlow.asStateFlow()

    override suspend fun getTicketById(id: Long): TicketEntity? {
        return _ticketsFlow.value.find { it.id == id }
    }

    override suspend fun insertTicket(ticket: TicketEntity) {
        val currentList = _ticketsFlow.value.toMutableList()
        val newId = if (currentList.isEmpty()) 1L else (currentList.maxOf { it.id } + 1L)
        val ticketWithId = ticket.copy(id = newId)
        currentList.add(0, ticketWithId)
        _ticketsFlow.value = currentList
        saveTicketsToFile(currentList)
    }

    override suspend fun updateTicket(ticket: TicketEntity) {
        val currentList = _ticketsFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == ticket.id }
        if (index != -1) {
            currentList[index] = ticket
            _ticketsFlow.value = currentList
            saveTicketsToFile(currentList)
        }
    }

    override suspend fun deleteTicket(id: Long) {
        val currentList = _ticketsFlow.value.toMutableList()
        val ticket = currentList.find { it.id == id }
        ticket?.fotoPath?.let { path ->
            try {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        currentList.removeAll { it.id == id }
        _ticketsFlow.value = currentList
        saveTicketsToFile(currentList)
    }

    override suspend fun getUserByEmail(email: String): UserEntity? {
        return usersMap[email]
    }

    override suspend fun insertUser(user: UserEntity) {
        usersMap[user.email] = user
        saveUsersToFile()
    }
}
