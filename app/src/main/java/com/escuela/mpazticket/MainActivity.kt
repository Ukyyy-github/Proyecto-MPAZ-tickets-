package com.escuela.mpazticket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.escuela.mpazticket.repository.LocalTicketRepository
import com.escuela.mpazticket.ui.screens.AddTicketScreen
import com.escuela.mpazticket.ui.screens.TicketDetailScreen
import com.escuela.mpazticket.ui.screens.TicketListScreen
import com.escuela.mpazticket.ui.theme.MPAZTicketTheme
import com.escuela.mpazticket.ui.viewmodel.TicketViewModel
import com.escuela.mpazticket.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = LocalTicketRepository(applicationContext)
        val factory = ViewModelFactory(repository)

        setContent {
            MPAZTicketTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(factory = factory)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val ticketViewModel: TicketViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = "ticket_list",
    ) {
        composable("ticket_list") {
            TicketListScreen(
                viewModel = ticketViewModel,
                onAddTicketClick = {
                    navController.navigate("add_ticket")
                },
                onTicketClick = { ticket ->
                    navController.navigate("ticket_detail/${ticket.id}")
                }
            )
        }

        composable("add_ticket") {
            AddTicketScreen(
                viewModel = ticketViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onTicketCreated = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "ticket_detail/{ticketId}",
            arguments = listOf(navArgument("ticketId") { type = NavType.LongType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getLong("ticketId") ?: 0L
            TicketDetailScreen(
                ticketId = ticketId,
                viewModel = ticketViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onTicketDeleted = {
                    navController.popBackStack()
                }
            )
        }
    }
}