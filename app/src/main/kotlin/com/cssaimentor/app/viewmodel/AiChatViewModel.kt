package com.cssaimentor.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cssaimentor.app.domain.model.ChatMessage
import com.cssaimentor.app.domain.model.ChatRole
import com.cssaimentor.app.domain.repository.AiRepository
import com.cssaimentor.app.utils.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val isTyping: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val aiRepository: AiRepository
) : ViewModel() {
    private val transient = MutableStateFlow(AiChatUiState())
    private var activeGeneration: Job? = null

    val uiState: StateFlow<AiChatUiState> = combine(
        aiRepository.observeChat(),
        transient
    ) { messages, state ->
        state.copy(
            messages = messages.ifEmpty { listOf(welcomeMessage) }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AiChatUiState(messages = listOf(welcomeMessage)))

    fun updateInput(value: String) {
        transient.update { it.copy(input = value, error = null) }
    }

    fun send() {
        val prompt = uiState.value.input.trim()
        if (prompt.isBlank() || uiState.value.isTyping) return
        activeGeneration = viewModelScope.launch {
            transient.update { it.copy(input = "", isTyping = true, error = null) }
            try {
                when (val result = aiRepository.sendMessage(prompt)) {
                    is AppResult.Success -> transient.update { it.copy(isTyping = false) }
                    is AppResult.Error -> transient.update { it.copy(isTyping = false, error = result.message) }
                }
            } catch (_: CancellationException) {
                transient.update { it.copy(isTyping = false, error = "Response stopped.") }
            } finally {
                activeGeneration = null
            }
        }
    }

    fun regenerate() {
        if (uiState.value.isTyping) return
        activeGeneration = viewModelScope.launch {
            transient.update { it.copy(isTyping = true, error = null) }
            try {
                when (val result = aiRepository.regenerateLastResponse()) {
                    is AppResult.Success -> transient.update { it.copy(isTyping = false) }
                    is AppResult.Error -> transient.update { it.copy(isTyping = false, error = result.message) }
                }
            } catch (_: CancellationException) {
                transient.update { it.copy(isTyping = false, error = "Response stopped.") }
            } finally {
                activeGeneration = null
            }
        }
    }

    fun stopGeneration() {
        activeGeneration?.cancel()
        activeGeneration = null
        transient.update { it.copy(isTyping = false, error = "Response stopped.") }
    }

    fun clearChat() {
        viewModelScope.launch { aiRepository.clearChat() }
    }

    private companion object {
        val welcomeMessage = ChatMessage(
            id = "welcome",
            role = ChatRole.Mentor,
            text = "## Welcome to CSS AI Mentor\nAsk for topic summaries, essay outlines, MCQs, answer structures, or a focused study plan.",
            createdAt = System.currentTimeMillis()
        )
    }
}
