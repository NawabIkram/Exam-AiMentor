package com.cssaimentor.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorText
import com.cssaimentor.app.ui.theme.MentorTextMuted

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    color: Color = MentorText
) {
    Column(modifier = modifier) {
        markdown.lines().forEach { rawLine ->
            val line = rawLine.trimEnd()
            when {
                line.isBlank() -> Spacer(Modifier.height(8.dp))
                line.startsWith("## ") -> Text(
                    text = line.removePrefix("## "),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MentorCyan
                )
                line.startsWith("# ") -> Text(
                    text = line.removePrefix("# "),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MentorCyan
                )
                line.startsWith("- ") -> Text(
                    text = styledMarkdown("• ${line.removePrefix("- ")}"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color
                )
                Regex("^\\d+\\.\\s.*").matches(line) -> Text(
                    text = styledMarkdown(line),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color
                )
                line.startsWith(">") -> Text(
                    text = line.removePrefix(">").trim(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MentorTextMuted
                )
                else -> Text(
                    text = styledMarkdown(line),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color
                )
            }
        }
    }
}

@Composable
fun CopyableMarkdownText(markdown: String, modifier: Modifier = Modifier) {
    val clipboard = LocalClipboardManager.current
    MarkdownText(
        markdown = markdown,
        modifier = modifier
    )
}

private fun styledMarkdown(line: String): AnnotatedString = buildAnnotatedString {
    var index = 0
    val regex = Regex("\\*\\*(.*?)\\*\\*")
    regex.findAll(line).forEach { match ->
        append(line.substring(index, match.range.first))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(match.groupValues[1])
        }
        index = match.range.last + 1
    }
    append(line.substring(index))
}

