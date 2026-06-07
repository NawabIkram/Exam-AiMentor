package com.cssaimentor.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cssaimentor.app.domain.model.QuizQuestion
import com.cssaimentor.app.domain.model.QuizResult
import com.cssaimentor.app.domain.model.QuizTopic
import com.cssaimentor.app.domain.repository.QuizRepository
import com.cssaimentor.app.utils.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val topics: List<QuizTopic> = emptyList(),
    val selectedTopic: QuizTopic? = null,
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: Int? = null,
    val correctAnswers: Int = 0,
    val remainingSeconds: Int = 0,
    val result: QuizResult? = null,
    val loading: Boolean = true,
    val error: String? = null
) {
    val currentQuestion: QuizQuestion? get() = questions.getOrNull(currentIndex)
    val inQuiz: Boolean get() = selectedTopic != null && result == null && questions.isNotEmpty()
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    private var timerJob: Job? = null

    init {
        loadTopics()
    }

    fun loadTopics() {
        viewModelScope.launch {
            _uiState.update { QuizUiState(loading = true) }
            when (val result = quizRepository.getTopics()) {
                is AppResult.Success -> _uiState.update { it.copy(topics = result.data, loading = false) }
                is AppResult.Error -> _uiState.update { it.copy(error = result.message, loading = false) }
            }
        }
    }

    fun start(topic: QuizTopic) {
        viewModelScope.launch {
            timerJob?.cancel()
            _uiState.update { it.copy(loading = true, error = null, result = null) }
            when (val result = quizRepository.getQuestions(topic.id)) {
                is AppResult.Success -> {
                    val seconds = (result.data.size.coerceAtLeast(1)) * 30
                    _uiState.update {
                        it.copy(
                            selectedTopic = topic,
                            questions = result.data,
                            currentIndex = 0,
                            selectedAnswer = null,
                            correctAnswers = 0,
                            remainingSeconds = seconds,
                            loading = false,
                            result = null
                        )
                    }
                    startTimer()
                }
                is AppResult.Error -> _uiState.update { it.copy(error = result.message, loading = false) }
            }
        }
    }

    fun selectAnswer(index: Int) {
        if (uiState.value.selectedAnswer != null) return
        val question = uiState.value.currentQuestion ?: return
        _uiState.update {
            it.copy(
                selectedAnswer = index,
                correctAnswers = it.correctAnswers + if (index == question.correctAnswerIndex) 1 else 0
            )
        }
    }

    fun next() {
        val state = uiState.value
        if (state.currentIndex >= state.questions.lastIndex) {
            finish()
        } else {
            _uiState.update { it.copy(currentIndex = it.currentIndex + 1, selectedAnswer = null) }
        }
    }

    fun finish() {
        val state = uiState.value
        val topic = state.selectedTopic ?: return
        val elapsed = state.questions.size * 30 - state.remainingSeconds
        val result = QuizResult(topic.title, state.questions.size, state.correctAnswers, elapsed)
        timerJob?.cancel()
        _uiState.update { it.copy(result = result, selectedAnswer = null) }
        viewModelScope.launch { quizRepository.saveResult(result) }
    }

    fun resetToTopics() {
        timerJob?.cancel()
        loadTopics()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (uiState.value.remainingSeconds > 0 && uiState.value.result == null) {
                delay(1000)
                _uiState.update { it.copy(remainingSeconds = (it.remainingSeconds - 1).coerceAtLeast(0)) }
            }
            if (uiState.value.remainingSeconds == 0 && uiState.value.result == null) finish()
        }
    }
}

