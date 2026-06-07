package com.cssaimentor.app.ui.screens.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cssaimentor.app.domain.model.ChatMessage
import com.cssaimentor.app.domain.model.ChatRole
import com.cssaimentor.app.ui.components.GlassCard
import com.cssaimentor.app.ui.components.MarkdownText
import com.cssaimentor.app.ui.components.MentorTextField
import com.cssaimentor.app.ui.components.ShimmerBlock
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorError
import com.cssaimentor.app.ui.theme.MentorPurple
import com.cssaimentor.app.ui.theme.MentorTextMuted
import com.cssaimentor.app.viewmodel.AiChatViewModel

@Composable
fun AiChatScreen(
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size, state.isTyping) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("AI Mentor", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("CSS-focused reasoning assistant", color = MentorTextMuted)
            }
            IconButton(onClick = viewModel::regenerate, enabled = !state.isTyping) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Regenerate", tint = MentorCyan)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.messages, key = { it.id }) { message ->
                MessageBubble(message)
            }
            if (state.isTyping) {
                item {
                    GlassCard(accent = MentorPurple, modifier = Modifier.fillMaxWidth(0.78f)) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ShimmerBlock(width = 160.dp)
                            ShimmerBlock(width = 220.dp)
                        }
                    }
                }
            }
        }
        if (state.error != null) {
            Text(state.error.orEmpty(), color = MentorError, modifier = Modifier.padding(vertical = 6.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MentorTextField(
                value = state.input,
                onValueChange = viewModel::updateInput,
                label = "Ask CSS AI Mentor",
                modifier = Modifier.weight(1f),
                singleLine = false
            )
            IconButton(
                onClick = {
                    if (state.isTyping) viewModel.stopGeneration() else viewModel.send()
                }
            ) {
                Icon(
                    imageVector = if (state.isTyping) Icons.Rounded.Close else Icons.Rounded.Send,
                    contentDescription = if (state.isTyping) "Stop response" else "Send",
                    tint = if (state.isTyping) MentorError else MentorCyan
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val clipboard = LocalClipboardManager.current
    val isUser = message.role == ChatRole.User
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        GlassCard(
            accent = if (isUser) MentorCyan else MentorPurple,
            modifier = Modifier.fillMaxWidth(if (isUser) 0.82f else 0.92f)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isUser) {
                    Text(message.text, style = MaterialTheme.typography.bodyMedium)
                } else {
                    MarkdownText(message.text)
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = { clipboard.setText(AnnotatedString(message.text)) }) {
                            Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy", tint = MentorTextMuted)
                        }
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(2.dp))
}
