package com.cssaimentor.app.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cssaimentor.app.ui.components.GlassCard
import com.cssaimentor.app.ui.components.MentorButton
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorPurple
import com.cssaimentor.app.ui.theme.MentorTextMuted
import com.cssaimentor.app.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val title: String,
    val body: String,
    val icon: ImageVector,
    val accent: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onDone: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        OnboardingPage("AI Preparation", "Get exam-focused explanations, essay outlines, MCQs, and study plans in seconds.", Icons.Rounded.AutoAwesome, MentorCyan),
        OnboardingPage("Past Papers", "Browse CSS papers by subject and year with favorites and distraction-free reading.", Icons.Rounded.Description, MentorPurple),
        OnboardingPage("Smart MCQ Practice", "Practice timed quizzes, check explanations, and build momentum every day.", Icons.Rounded.Quiz, Color(0xFF22C55E))
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 36.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { viewModel.complete(onDone) }) {
                Text("Skip", color = MentorTextMuted)
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }

        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                pages.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(width = if (index == pagerState.currentPage) 28.dp else 8.dp, height = 8.dp)
                            .background(
                                if (index == pagerState.currentPage) MentorCyan else MentorTextMuted.copy(alpha = 0.32f),
                                CircleShape
                            )
                    )
                }
            }
            MentorButton(
                text = if (pagerState.currentPage == pages.lastIndex) "Start Preparing" else "Next",
                onClick = {
                    if (pagerState.currentPage == pages.lastIndex) {
                        viewModel.complete(onDone)
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlassCard(accent = page.accent) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .padding(34.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = page.accent,
                    modifier = Modifier.size(92.dp)
                )
            }
        }
        Spacer(Modifier.height(36.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = page.body,
            style = MaterialTheme.typography.bodyLarge,
            color = MentorTextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

