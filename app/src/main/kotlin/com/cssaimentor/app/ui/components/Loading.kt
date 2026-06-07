package com.cssaimentor.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cssaimentor.app.ui.theme.MentorSurfaceHigh

@Composable
fun ShimmerBlock(
    modifier: Modifier = Modifier,
    width: Dp = 120.dp,
    height: Dp = 18.dp
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.78f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "shimmerAlpha"
    )
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .alpha(alpha)
            .background(MentorSurfaceHigh, RoundedCornerShape(8.dp))
    )
}

