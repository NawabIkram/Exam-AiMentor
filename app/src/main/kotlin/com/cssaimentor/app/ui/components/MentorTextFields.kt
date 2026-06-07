package com.cssaimentor.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorLine
import com.cssaimentor.app.ui.theme.MentorSurfaceHigh
import com.cssaimentor.app.ui.theme.MentorText
import com.cssaimentor.app.ui.theme.MentorTextMuted

@Composable
fun MentorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MentorText,
            unfocusedTextColor = MentorText,
            focusedBorderColor = MentorCyan,
            unfocusedBorderColor = MentorLine,
            focusedContainerColor = MentorSurfaceHigh.copy(alpha = 0.58f),
            unfocusedContainerColor = MentorSurfaceHigh.copy(alpha = 0.42f),
            cursorColor = MentorCyan,
            focusedLabelColor = MentorCyan,
            unfocusedLabelColor = MentorTextMuted
        )
    )
}

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = {
            Text(placeholder, color = MentorTextMuted, style = MaterialTheme.typography.bodyMedium)
        },
        leadingIcon = {
            Icon(Icons.Rounded.Search, contentDescription = null, tint = MentorTextMuted)
        },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MentorText,
            unfocusedTextColor = MentorText,
            focusedBorderColor = MentorCyan,
            unfocusedBorderColor = MentorLine,
            focusedContainerColor = MentorSurfaceHigh.copy(alpha = 0.58f),
            unfocusedContainerColor = MentorSurfaceHigh.copy(alpha = 0.42f),
            cursorColor = MentorCyan
        )
    )
}

