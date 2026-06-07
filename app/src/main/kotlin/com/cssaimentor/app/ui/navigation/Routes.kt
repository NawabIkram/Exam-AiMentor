package com.cssaimentor.app.ui.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Route(val route: String) {
    data object Splash : Route("splash")
    data object Onboarding : Route("onboarding")
    data object Login : Route("login")
    data object Signup : Route("signup")
    data object Home : Route("home")
    data object AiChat : Route("ai_chat")
    data object Papers : Route("papers")
    data object Quiz : Route("quiz")
    data object Books : Route("books")
    data object Profile : Route("profile")
    data object PdfViewer : Route("pdf/{documentId}/{title}/{url}") {
        fun create(documentId: String, title: String, url: String): String =
            "pdf/${Uri.encode(documentId)}/${Uri.encode(title)}/${Uri.encode(url)}"
    }
}

data class BottomNavItem(
    val route: Route,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Route.Home, "Home", Icons.Rounded.Home),
    BottomNavItem(Route.AiChat, "AI", Icons.Rounded.AutoAwesome),
    BottomNavItem(Route.Papers, "Papers", Icons.Rounded.WorkspacePremium),
    BottomNavItem(Route.Quiz, "Quiz", Icons.Rounded.Quiz),
    BottomNavItem(Route.Books, "Books", Icons.Rounded.Book),
    BottomNavItem(Route.Profile, "Profile", Icons.Rounded.Person)
)

fun String?.isBottomRoute(): Boolean = bottomNavItems.any { it.route.route == this }

