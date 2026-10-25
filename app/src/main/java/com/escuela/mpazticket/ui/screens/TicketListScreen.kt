package com.escuela.mpazticket.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.escuela.mpazticket.data.local.entities.TicketEntity
import com.escuela.mpazticket.ui.components.TicketItem
import com.escuela.mpazticket.ui.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListScreen(
    viewModel: TicketViewModel,
    onAddTicketClick: () -> Unit,
    onTicketClick: (TicketEntity) -> Unit
) {
    val ticketsList by viewModel.ticketsList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MPAZ Ticket - Mantenimiento") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTicketClick) {
                Text(
                    text = "+",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (ticketsList.isEmpty()) {
                Text(
                    text = "No hay tickets de mantenimiento registrados.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = ticketsList,
                        key = { ticket -> ticket.id }
                    ) { ticket ->
                        TicketItem(
                            ticket = ticket,
                            onTicketClick = onTicketClick
                        )
                    }
                }
            }
        }
    }
}