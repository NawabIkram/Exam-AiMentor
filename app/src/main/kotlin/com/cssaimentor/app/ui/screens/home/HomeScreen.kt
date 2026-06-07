package com.cssaimentor.app.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cssaimentor.app.domain.model.DashboardStats
import com.cssaimentor.app.ui.components.GlassCard
import com.cssaimentor.app.ui.components.MetricPill
import com.cssaimentor.app.ui.components.PressableGlassCard
import com.cssaimentor.app.ui.components.SectionHeader
import com.cssaimentor.app.ui.theme.MentorAmber
import com.cssaimentor.app.ui.theme.MentorBlue
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorGreen
import com.cssaimentor.app.ui.theme.MentorLine
import com.cssaimentor.app.ui.theme.MentorPurple
import com.cssaimentor.app.ui.theme.MentorTextMuted
import com.cssaimentor.app.viewmodel.HomeViewModel

private data class Feature(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    onOpenAi: () -> Unit,
    onOpenPapers: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenBooks: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val features = listOf(
        Feature("AI Mentor", "Ask, outline, revise", Icons.Rounded.AutoAwesome, MentorCyan, onOpenAi),
        Feature("Past Papers", "Subject and year wise", Icons.Rounded.Description, MentorPurple, onOpenPapers),
        Feature("MCQ Quiz", "Timed practice", Icons.Rounded.Quiz, MentorGreen, onOpenQuiz),
        Feature("Books Library", "Notes and PDFs", Icons.Rounded.Book, MentorAmber, onOpenBooks)
    )

    LazyColumn(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { Spacer(Modifier.height(18.dp)) }
        item { Greeting(stats) }
        item { HeroAskCard(onOpenAi) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricPill("Study streak", "${stats.streak} days", Modifier.weight(1f), MentorCyan)
                MetricPill("Accuracy", "${(stats.quizAccuracy * 100).toInt()}%", Modifier.weight(1f), MentorGreen)
            }
        }
        item {
            GlassCard(accent = MentorBlue) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Weekly progress", style = MaterialTheme.typography.titleMedium)
                        Text("${stats.studyMinutes} focused minutes logged", color = MentorTextMuted)
                    }
                    ProgressRing(stats.weeklyProgress)
                }
            }
        }
        item { SectionHeader("Prep cockpit", "Choose your next move") }
        items(features.chunked(2)) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { feature ->
                    FeatureCard(feature, Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        item { Spacer(Modifier.height(18.dp)) }
    }
}

@Composable
private fun Greeting(stats: DashboardStats) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Assalam o Alaikum,", style = MaterialTheme.typography.bodyLarge, color = MentorTextMuted)
        Text(
            text = stats.greetingName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text("Your focused CSS preparation starts here.", color = MentorTextMuted)
    }
}

@Composable
private fun HeroAskCard(onClick: () -> Unit) {
    PressableGlassCard(accent = MentorCyan, onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(Icons.Rounded.Search, contentDescription = null, tint = MentorCyan, modifier = Modifier.size(32.dp))
            Column {
                Text("Ask AI anything about CSS...", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Essay ideas, MCQs, summaries, or answer structure", color = MentorTextMuted)
            }
        }
    }
}

@Composable
private fun FeatureCard(feature: Feature, modifier: Modifier = Modifier) {
    PressableGlassCard(accent = feature.accent, onClick = feature.onClick, modifier = modifier.height(154.dp)) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(feature.icon, contentDescription = null, tint = feature.accent, modifier = Modifier.size(32.dp))
            Column {
                Text(feature.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(feature.subtitle, color = MentorTextMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ProgressRing(progress: Float) {
    val animated by animateFloatAsState(progress, label = "progress")
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(76.dp)) {
        Canvas(modifier = Modifier.size(76.dp)) {
            drawCircle(
                color = MentorLine,
                radius = size.minDimension / 2,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = MentorCyan,
                startAngle = -90f,
                sweepAngle = animated.coerceIn(0f, 1f) * 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                topLeft = Offset.Zero,
                size = size
            )
        }
        Text("${(animated * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
    }
}

