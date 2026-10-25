package com.escuela.mpazticket.data.local

import androidx.room.TypeConverter
import com.escuela.mpazticket.data.local.entities.Priority
import com.escuela.mpazticket.data.local.entities.TicketStatus
import com.escuela.mpazticket.data.local.entities.TrafficLightColor
import com.escuela.mpazticket.data.local.entities.UserRole

class Converters {

    @TypeConverter
    fun fromPriority(value: Priority?): String? = value?.name

    @TypeConverter
    fun toPriority(value: String?): Priority? = value?.let {
        runCatching { enumValueOf<Priority>(it) }.getOrDefault(Priority.MEDIA)
    }

    @TypeConverter
    fun fromTicketStatus(value: TicketStatus?): String? = value?.name

    @TypeConverter
    fun toTicketStatus(value: String?): TicketStatus? = value?.let {
        runCatching { enumValueOf<TicketStatus>(it) }.getOrDefault(TicketStatus.PENDIENTE)
    }

    @TypeConverter
    fun fromUserRole(value: UserRole?): String? = value?.name

    @TypeConverter
    fun toUserRole(value: String?): UserRole? = value?.let {
        runCatching { enumValueOf<UserRole>(it) }.getOrDefault(UserRole.SOLICITANTE)
    }

    @TypeConverter
    fun fromTrafficLightColor(value: TrafficLightColor?): String? = value?.name

    @TypeConverter
    fun toTrafficLightColor(value: String?): TrafficLightColor? = value?.let {
        runCatching { enumValueOf<TrafficLightColor>(it) }.getOrDefault(TrafficLightColor.VERDE)
    }
}
