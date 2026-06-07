package com.cssaimentor.app.domain.model

enum class ChatRole {
    User,
    Mentor
}

data class ChatMessage(
    val id: String,
    val role: ChatRole,
    val text: String,
    val createdAt: Long,
    val isLoading: Boolean = false
)

