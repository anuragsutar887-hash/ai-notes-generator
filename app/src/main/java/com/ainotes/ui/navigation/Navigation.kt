package com.ainotes.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ainotes.ui.screens.history.HistoryScreen
import com.ainotes.ui.screens.home.HomeScreen
import com.ainotes.ui.screens.login.LoginScreen
import com.ainotes.ui.screens.profile.ProfileScreen
import com.ainotes.ui.screens.profile.ProfileSetupScreen
import com.ainotes.ui.screens.profile.SplashScreen
import com.ainotes.ui.screens.results.ResultsScreen
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object ProfileSetup : Screen("profile_setup")
    object ProfileEdit : Screen("profile_edit")
    object Results : Screen("results/{sessionId}") {
        fun createRoute(sessionId: String) = "results/$sessionId"
    }
    object History : Screen("history")
}

@Composable
fun AiNotesNavGraph() {
    val navController = rememberNavController()
    val auth = remember { FirebaseAuth.getInstance() }
    var userState by remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            userState = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }

    LaunchedEffect(userState) {
        if (userState == null) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val startDestination = Screen.Splash.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToProfileSetup = {
                    navController.navigate(Screen.ProfileSetup.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    // Check profile setup after sign in
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        // Let Splash screen handle the routing logic dynamically
                        navController.navigate(Screen.Splash.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.ProfileSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProfileEdit.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToResults = { sessionId ->
                    navController.navigate(Screen.Results.createRoute(sessionId))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.ProfileEdit.route)
                }
            )
        }

        composable(
            route = Screen.Results.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            ResultsScreen(
                sessionId = sessionId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToResults = { sessionId ->
                    navController.navigate(Screen.Results.createRoute(sessionId))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
