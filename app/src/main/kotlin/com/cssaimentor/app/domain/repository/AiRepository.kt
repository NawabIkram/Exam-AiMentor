package com.cssaimentor.app.domain.repository

import com.cssaimentor.app.domain.model.ChatMessage
import com.cssaimentor.app.utils.AppResult
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    fun observeChat(): Flow<List<ChatMessage>>
    suspend fun sendMessage(message: String): AppResult<ChatMessage>
    suspend fun regenerateLastResponse(): AppResult<ChatMessage>
    suspend fun clearChat()
}

