package com.escuela.mpazticket.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.escuela.mpazticket.data.local.entities.TicketEntity
import com.escuela.mpazticket.ui.components.bounceClick
import com.escuela.mpazticket.ui.viewmodel.AuthViewModel
import com.escuela.mpazticket.ui.viewmodel.TicketViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    ticketViewModel: TicketViewModel,
    onAddTicketClick: () -> Unit,
    onTicketClick: (TicketEntity) -> Unit,
    onLogoutDone: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Tickets, 1: Perfil
    val currentUser by authViewModel.currentUser.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header in Drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.nombreCompleto?.take(1)?.uppercase() ?: "U",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        Text(
                            text = currentUser?.nombreCompleto ?: "Usuario Invitado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = currentUser?.email ?: "admin@mpazticket.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                NavigationDrawerItem(
                    label = { Text("Tickets de Mantenimiento") },
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .bounceClick()
                )

                NavigationDrawerItem(
                    label = { Text("Mi Perfil y Cuenta") },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .bounceClick()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = "Cerrar Sesión",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        authViewModel.logout(onLogoutDone)
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .bounceClick()
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (selectedTab) {
                                0 -> "MPAZ Ticket - Mantenimiento"
                                else -> "Perfil de Usuario"
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.bounceClick()
                        ) {
                            Text(text = "≡", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Text(text = "🎫", fontSize = 18.sp) },
                        label = { Text("Tickets") },
                        modifier = Modifier.bounceClick()
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Text(text = "👤", fontSize = 18.sp) },
                        label = { Text("Perfil") },
                        modifier = Modifier.bounceClick()
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally(animationSpec = tween(300), initialOffsetX = { fullWidth -> fullWidth }) + fadeIn(animationSpec = tween(300)))
                                .togetherWith(slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut(animationSpec = tween(300)))
                        } else {
                            (slideInHorizontally(animationSpec = tween(300), initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn(animationSpec = tween(300)))
                                .togetherWith(slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { fullWidth -> fullWidth }) + fadeOut(animationSpec = tween(300)))
                        }
                    },
                    label = "tabChangeAnimation"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> TicketListScreen(
                            viewModel = ticketViewModel,
                            onAddTicketClick = onAddTicketClick,
                            onTicketClick = onTicketClick
                        )
                        else -> ProfileScreen(
                            authViewModel = authViewModel,
                            onLogoutDone = onLogoutDone
                        )
                    }
                }
            }
        }
    }
}
