package com.cssaimentor.app.domain.model

data class UserProfile(
    val uid: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val streak: Int = 0,
    val progress: Float = 0f,
    val completedQuizzes: Int = 0,
    val totalStudyMinutes: Int = 0
)

