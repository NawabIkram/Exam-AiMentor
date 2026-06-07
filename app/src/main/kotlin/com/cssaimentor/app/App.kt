package com.cssaimentor.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.cssaimentor.app.ui.components.PremiumBackground
import com.cssaimentor.app.ui.navigation.MentorNavGraph
import com.cssaimentor.app.ui.theme.CSSAIMentorTheme

@Composable
fun CSSAIMentorApp() {
    CSSAIMentorTheme {
        PremiumBackground {
            MentorNavGraph(navController = rememberNavController())
        }
    }
}

