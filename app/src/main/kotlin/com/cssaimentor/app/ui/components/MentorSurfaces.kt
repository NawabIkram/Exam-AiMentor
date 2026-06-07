package com.cssaimentor.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cssaimentor.app.ui.theme.MentorBlack
import com.cssaimentor.app.ui.theme.MentorBlue
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorLine
import com.cssaimentor.app.ui.theme.MentorPurple
import com.cssaimentor.app.ui.theme.MentorSurface
import com.cssaimentor.app.ui.theme.MentorSurfaceHigh
import com.cssaimentor.app.ui.theme.MentorText
import com.cssaimentor.app.ui.theme.MentorTextMuted

@Composable
fun PremiumBackground(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalContentColor provides MentorText) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(MentorBlack, Color(0xFF080A10), MentorBlack)
                    )
                )
        ) {
            content()
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    accent: Color = MentorCyan,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MentorSurface.copy(alpha = 0.86f)),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(accent.copy(alpha = 0.55f), MentorLine.copy(alpha = 0.75f), MentorPurple.copy(alpha = 0.22f))
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@Composable
fun PressableGlassCard(
    modifier: Modifier = Modifier,
    accent: Color = MentorCyan,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, label = "cardPress")

    GlassCard(
        accent = accent,
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        content = content
    )
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MentorTextMuted
                )
            }
        }
        if (action != null) action()
    }
}

@Composable
fun MetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Color = MentorBlue
) {
    GlassCard(modifier = modifier, accent = accent) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MentorTextMuted)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SurfaceBand(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(MentorSurfaceHigh.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        content()
    }
}
