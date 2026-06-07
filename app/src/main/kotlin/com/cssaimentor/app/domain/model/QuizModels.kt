package com.cssaimentor.app.domain.model

data class QuizTopic(
    val id: String,
    val title: String,
    val subtitle: String,
    val questionCount: Int,
    val accent: Long
)

data class QuizQuestion(
    val id: String,
    val topicId: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

data class QuizResult(
    val topicTitle: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val elapsedSeconds: Int
) {
    val scorePercent: Int
        get() = if (totalQuestions == 0) 0 else ((correctAnswers.toFloat() / totalQuestions) * 100).toInt()
}

