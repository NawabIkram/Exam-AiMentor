package com.cssaimentor.app.ui.screens.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cssaimentor.app.domain.model.QuizQuestion
import com.cssaimentor.app.domain.model.QuizResult
import com.cssaimentor.app.domain.model.QuizTopic
import com.cssaimentor.app.ui.components.GlassCard
import com.cssaimentor.app.ui.components.MentorButton
import com.cssaimentor.app.ui.components.SectionHeader
import com.cssaimentor.app.ui.components.ShimmerBlock
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorError
import com.cssaimentor.app.ui.theme.MentorGreen
import com.cssaimentor.app.ui.theme.MentorLine
import com.cssaimentor.app.ui.theme.MentorPurple
import com.cssaimentor.app.ui.theme.MentorSurfaceHigh
import com.cssaimentor.app.ui.theme.MentorText
import com.cssaimentor.app.ui.theme.MentorTextMuted
import com.cssaimentor.app.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val result = state.result

    when {
        state.loading -> QuizLoading()
        result != null -> ResultView(result, onDone = viewModel::resetToTopics)
        state.inQuiz -> QuestionView(
            question = state.currentQuestion,
            index = state.currentIndex,
            total = state.questions.size,
            selected = state.selectedAnswer,
            remainingSeconds = state.remainingSeconds,
            onSelect = viewModel::selectAnswer,
            onNext = viewModel::next,
            onFinish = viewModel::finish
        )
        else -> TopicList(
            topics = state.topics,
            error = state.error,
            onStart = viewModel::start
        )
    }
}

@Composable
private fun TopicList(
    topics: List<QuizTopic>,
    error: String?,
    onStart: (QuizTopic) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }
        item { SectionHeader("MCQ Practice", "Timed, gamified preparation") }
        if (error != null) item { Text(error, color = MentorError) }
        items(topics, key = { it.id }) { topic ->
            GlassCard(accent = Color(topic.accent)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(topic.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(topic.subtitle, color = MentorTextMuted)
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("${topic.questionCount} questions", color = MentorCyan)
                        MentorButton(text = "Start", onClick = { onStart(topic) })
                    }
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun QuestionView(
    question: QuizQuestion?,
    index: Int,
    total: Int,
    selected: Int?,
    remainingSeconds: Int,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    if (question == null) return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Question ${index + 1}/$total", color = MentorTextMuted)
            Text("${remainingSeconds}s", color = MentorCyan, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { (index + 1).toFloat() / total.coerceAtLeast(1) },
            modifier = Modifier.fillMaxWidth(),
            color = MentorCyan,
            trackColor = MentorLine
        )
        GlassCard(accent = MentorPurple) {
            Text(
                question.question,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(18.dp)
            )
        }
        question.options.forEachIndexed { optionIndex, option ->
            val isCorrect = selected != null && optionIndex == question.correctAnswerIndex
            val isWrong = selected == optionIndex && optionIndex != question.correctAnswerIndex
            val borderColor = when {
                isCorrect -> MentorGreen
                isWrong -> MentorError
                selected == optionIndex -> MentorCyan
                else -> MentorLine
            }
            Card(
                onClick = { onSelect(optionIndex) },
                enabled = selected == null,
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MentorSurfaceHigh.copy(alpha = 0.62f)),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Text(
                    text = option,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = MentorText
                )
            }
        }
        if (selected != null) {
            Text(question.explanation, color = MentorTextMuted)
            MentorButton(
                text = if (index == total - 1) "Finish" else "Next",
                onClick = if (index == total - 1) onFinish else onNext,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ResultView(result: QuizResult, onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        GlassCard(accent = MentorGreen) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Quiz Complete", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(result.topicTitle, color = MentorTextMuted)
                Text("${result.scorePercent}%", style = MaterialTheme.typography.displaySmall, color = MentorCyan, fontWeight = FontWeight.Bold)
                Text("${result.correctAnswers}/${result.totalQuestions} correct • ${result.elapsedSeconds}s", color = MentorTextMuted)
                MentorButton("Back to topics", onClick = onDone, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun QuizLoading() {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Spacer(Modifier.height(18.dp))
        ShimmerBlock(width = 180.dp, height = 28.dp)
        repeat(3) {
            GlassCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShimmerBlock(width = 220.dp)
                    ShimmerBlock(width = 160.dp)
                }
            }
        }
    }
}
