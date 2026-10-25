package com.escuela.mpazticket.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.escuela.mpazticket.data.local.entities.*
import com.escuela.mpazticket.ui.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTicketScreen(
    viewModel: TicketViewModel,
    onBackClick: () -> Unit,
    onTicketCreated: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Mantenimiento General") }
    var prioridadSeleccionada by remember { mutableStateOf(Priority.MEDIA) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Ticket") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "<",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título del Problema") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación (Ej: Pabellón B, Sala 12)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción Detallada") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Text(
                text = "Gravedad del Problema",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Priority.entries.forEach { priority ->
                    FilterChip(
                        selected = prioridadSeleccionada == priority,
                        onClick = { prioridadSeleccionada = priority },
                        label = { Text(priority.getDisplayName()) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (titulo.isNotBlank() && ubicacion.isNotBlank()) {
                        viewModel.registrarTicket(
                            titulo = titulo,
                            descripcion = descripcion,
                            ubicacion = ubicacion,
                            categoria = categoria,
                            prioridadInicial = prioridadSeleccionada,
                            creadoPorEmail = "usuario@escuela.cl"
                        )
                        onTicketCreated()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = titulo.isNotBlank() && ubicacion.isNotBlank()
            ) {
                Text("Guardar Ticket")
            }
        }
    }
}