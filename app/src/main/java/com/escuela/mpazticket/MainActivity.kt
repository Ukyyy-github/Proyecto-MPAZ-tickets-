package com.escuela.mpazticket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.escuela.mpazticket.ui.screens.LoginScreen
import com.escuela.mpazticket.ui.screens.MainScreen
import com.escuela.mpazticket.ui.screens.RegisterScreen
import com.escuela.mpazticket.ui.screens.TicketDetailScreen
import com.escuela.mpazticket.ui.theme.MPAZTicketTheme
import com.escuela.mpazticket.ui.viewmodel.AuthViewModel
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
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val ticketViewModel: TicketViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300), initialOffsetX = { 300 }) },
        exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { -300 }) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300), initialOffsetX = { -300 }) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { 300 }) }
    ) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                authViewModel = authViewModel,
                ticketViewModel = ticketViewModel,
                onAddTicketClick = {
                    navController.navigate("add_ticket")
                },
                onTicketClick = { ticket ->
                    navController.navigate("ticket_detail/${ticket.id}")
                },
                onLogoutDone = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
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
