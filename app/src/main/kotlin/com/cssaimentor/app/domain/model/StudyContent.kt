package com.cssaimentor.app.domain.model

enum class ContentType {
    Paper,
    Book
}

data class Paper(
    val id: String,
    val subject: String,
    val year: Int,
    val title: String,
    val pdfUrl: String,
    val thumbnailUrl: String? = null,
    val isFavorite: Boolean = false
)

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val category: String,
    val pdfUrl: String,
    val coverUrl: String? = null,
    val isFavorite: Boolean = false
)

data class DashboardStats(
    val greetingName: String,
    val streak: Int,
    val weeklyProgress: Float,
    val studyMinutes: Int,
    val quizAccuracy: Float
)

