package com.cssaimentor.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cssaimentor.app.domain.model.ContentType
import com.cssaimentor.app.domain.model.Paper
import com.cssaimentor.app.domain.repository.ContentRepository
import com.cssaimentor.app.utils.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PapersUiState(
    val papers: List<Paper> = emptyList(),
    val subjects: List<String> = listOf("All"),
    val selectedSubject: String = "All",
    val query: String = "",
    val loading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PapersViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PapersUiState())
    val uiState: StateFlow<PapersUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun updateSearch(query: String) {
        _uiState.update { it.copy(query = query) }
        load()
    }

    fun selectSubject(subject: String) {
        _uiState.update { it.copy(selectedSubject = subject) }
        load()
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            contentRepository.toggleFavorite(id, ContentType.Paper)
            load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            val current = uiState.value
            _uiState.update { it.copy(loading = true, error = null) }
            when (val result = contentRepository.getPapers(current.query, current.selectedSubject)) {
                is AppResult.Success -> {
                    val allSubjects = listOf("All") + result.data.map { it.subject }.distinct().sorted()
                    _uiState.update { it.copy(papers = result.data, subjects = allSubjects, loading = false) }
                }
                is AppResult.Error -> _uiState.update { it.copy(error = result.message, loading = false) }
            }
        }
    }
}

