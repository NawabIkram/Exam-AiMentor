package com.cssaimentor.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.cssaimentor.app.ui.components.MentorBottomBar
import com.cssaimentor.app.ui.screens.ai.AiChatScreen
import com.cssaimentor.app.ui.screens.auth.LoginScreen
import com.cssaimentor.app.ui.screens.auth.SignupScreen
import com.cssaimentor.app.ui.screens.books.BooksScreen
import com.cssaimentor.app.ui.screens.home.HomeScreen
import com.cssaimentor.app.ui.screens.onboarding.OnboardingScreen
import com.cssaimentor.app.ui.screens.papers.PapersScreen
import com.cssaimentor.app.ui.screens.pdf.PdfViewerScreen
import com.cssaimentor.app.ui.screens.profile.ProfileScreen
import com.cssaimentor.app.ui.screens.quiz.QuizScreen
import com.cssaimentor.app.ui.screens.splash.SplashScreen

@Composable
fun MentorNavGraph(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (currentRoute.isBottomRoute()) {
                MentorBottomBar(currentRoute = currentRoute, navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Splash.route) {
                SplashScreen(
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Route.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Route.Onboarding.route) {
                OnboardingScreen(
                    onDone = {
                        navController.navigate(Route.Login.route) {
                            popUpTo(Route.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Route.Login.route) {
                LoginScreen(
                    onSignup = { navController.navigate(Route.Signup.route) },
                    onLoggedIn = {
                        navController.navigate(Route.Home.route) {
                            popUpTo(Route.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Route.Signup.route) {
                SignupScreen(
                    onLogin = { navController.popBackStack() },
                    onSignedUp = {
                        navController.navigate(Route.Home.route) {
                            popUpTo(Route.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Route.Home.route) {
                HomeScreen(
                    onOpenAi = { navController.navigate(Route.AiChat.route) },
                    onOpenPapers = { navController.navigate(Route.Papers.route) },
                    onOpenQuiz = { navController.navigate(Route.Quiz.route) },
                    onOpenBooks = { navController.navigate(Route.Books.route) }
                )
            }
            composable(Route.AiChat.route) {
                AiChatScreen()
            }
            composable(Route.Papers.route) {
                PapersScreen(
                    onOpenPdf = { id, title, url -> navController.navigate(Route.PdfViewer.create(id, title, url)) }
                )
            }
            composable(Route.Quiz.route) {
                QuizScreen()
            }
            composable(Route.Books.route) {
                BooksScreen(
                    onOpenPdf = { id, title, url -> navController.navigate(Route.PdfViewer.create(id, title, url)) }
                )
            }
            composable(Route.Profile.route) {
                ProfileScreen(
                    onLoggedOut = {
                        navController.navigate(Route.Login.route) {
                            popUpTo(0)
                        }
                    }
                )
            }
            composable(
                route = Route.PdfViewer.route,
                arguments = listOf(
                    navArgument("documentId") { type = NavType.StringType },
                    navArgument("title") { type = NavType.StringType },
                    navArgument("url") { type = NavType.StringType }
                )
            ) {
                PdfViewerScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

