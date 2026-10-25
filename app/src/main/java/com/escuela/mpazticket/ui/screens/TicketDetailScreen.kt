package com.escuela.mpazticket.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.escuela.mpazticket.data.local.entities.Priority
import com.escuela.mpazticket.data.local.entities.TicketStatus
import com.escuela.mpazticket.ui.state.SemaforoEvaluator
import com.escuela.mpazticket.ui.viewmodel.TicketViewModel

fun Priority.getDisplayName(): String = when (this) {
    Priority.BAJA -> "Baja"
    Priority.MEDIA -> "Media"
    Priority.ALTA -> "Alta"
}

fun TicketStatus.getDisplayName(): String = when (this) {
    TicketStatus.PENDIENTE -> "Pendiente"
    TicketStatus.REVISADO -> "Revisado"
    TicketStatus.ASIGNADO -> "Asignado"
    TicketStatus.EN_PROCESO -> "En Proceso"
    TicketStatus.SOLUCIONADO -> "Solucionado"
    TicketStatus.CERRADO -> "Cerrado"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticketId: Long,
    viewModel: TicketViewModel,
    onBackClick: () -> Unit,
    onTicketDeleted: () -> Unit
) {
    val tickets by viewModel.ticketsList.collectAsState()
    val ticket = tickets.find { it.id == ticketId }

    val semaforoInfo = ticket?.let { SemaforoEvaluator.calcularUrgencia(it.fechaCreacionTimestamp) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Ticket") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text(text = "<", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (ticket == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Ticket no encontrado.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(color = semaforoInfo?.color ?: MaterialTheme.colorScheme.primary, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = ticket.titulo,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Categoría: ${ticket.categoria}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Ubicación: ${ticket.ubicacion}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Gravedad: ${ticket.prioridadInicial.getDisplayName()}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Estado Actual: ${ticket.estado.getDisplayName()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Urgencia: ${semaforoInfo?.etiqueta}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Reportado por: ${ticket.creadoPorEmail}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Text(
                    text = "Descripción:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = ticket.descripcion,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Actualizar Estado:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TicketStatus.entries.forEach { status ->
                        val isSelected = ticket.estado == status
                        Surface(
                            onClick = {
                                viewModel.actualizarEstadoTicket(ticket, status)
                            },
                            shape = MaterialTheme.shapes.medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            tonalElevation = if (isSelected) 4.dp else 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = status.getDisplayName(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isSelected) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text(
                                            text = "Actual",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.eliminarTicket(ticket.id)
                        onTicketDeleted()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar Ticket")
                }
            }
        }
    }
}
