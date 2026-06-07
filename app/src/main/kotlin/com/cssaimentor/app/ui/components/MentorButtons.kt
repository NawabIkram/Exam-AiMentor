package com.cssaimentor.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cssaimentor.app.ui.theme.MentorBlack
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorLine
import com.cssaimentor.app.ui.theme.MentorSurfaceHigh
import com.cssaimentor.app.ui.theme.MentorText

@Composable
fun MentorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable RowScope.() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 52.dp),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MentorCyan,
            contentColor = MentorBlack,
            disabledContainerColor = MentorSurfaceHigh,
            disabledContentColor = MentorText.copy(alpha = 0.55f)
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = MentorBlack,
                strokeWidth = 2.dp,
                modifier = Modifier.defaultMinSize(18.dp, 18.dp)
            )
        } else {
            if (leadingIcon != null) leadingIcon()
            Text(text)
        }
    }
}

@Composable
fun MentorOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable RowScope.() -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 50.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MentorText,
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, MentorLine),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 13.dp)
    ) {
        if (leadingIcon != null) leadingIcon()
        Text(text)
    }
}

