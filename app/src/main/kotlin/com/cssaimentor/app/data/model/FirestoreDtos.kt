package com.cssaimentor.app.data.model

import com.cssaimentor.app.domain.model.Book
import com.cssaimentor.app.domain.model.Paper
import com.cssaimentor.app.domain.model.QuizQuestion
import com.cssaimentor.app.domain.model.UserProfile

data class UserDto(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val streak: Int = 0,
    val progress: Float = 0f,
    val completedQuizzes: Int = 0,
    val totalStudyMinutes: Int = 0
) {
    fun toDomain() = UserProfile(uid, name, email, photoUrl, streak, progress, completedQuizzes, totalStudyMinutes)
}

data class PaperDto(
    val id: String = "",
    val subject: String = "",
    val year: Int = 0,
    val title: String = "",
    val pdfUrl: String = "",
    val thumbnailUrl: String? = null
) {
    fun toDomain(isFavorite: Boolean = false) = Paper(id, subject, year, title, pdfUrl, thumbnailUrl, isFavorite)
}

data class BookDto(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val category: String = "",
    val pdfUrl: String = "",
    val coverUrl: String? = null
) {
    fun toDomain(isFavorite: Boolean = false) = Book(id, title, author, category, pdfUrl, coverUrl, isFavorite)
}

data class QuizQuestionDto(
    val id: String = "",
    val topicId: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0,
    val explanation: String = ""
) {
    fun toDomain() = QuizQuestion(id, topicId, question, options, correctAnswerIndex, explanation)
}

