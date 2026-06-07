package com.cssaimentor.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.cssaimentor.app.ui.navigation.bottomNavItems
import com.cssaimentor.app.ui.theme.MentorBlack
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorLine
import com.cssaimentor.app.ui.theme.MentorSurface
import com.cssaimentor.app.ui.theme.MentorTextMuted

@Composable
fun MentorBottomBar(
    currentRoute: String?,
    navController: NavController
) {
    NavigationBar(
        modifier = Modifier
            .height(76.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .border(BorderStroke(1.dp, MentorLine), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        containerColor = MentorSurface.copy(alpha = 0.96f),
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MentorBlack,
                    selectedTextColor = MentorCyan,
                    indicatorColor = MentorCyan,
                    unselectedIconColor = MentorTextMuted,
                    unselectedTextColor = MentorTextMuted
                )
            )
        }
    }
}

