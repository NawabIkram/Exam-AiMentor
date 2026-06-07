package com.cssaimentor.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cssaimentor.app.domain.model.Book
import com.cssaimentor.app.domain.model.ContentType
import com.cssaimentor.app.domain.repository.ContentRepository
import com.cssaimentor.app.utils.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BooksUiState(
    val books: List<Book> = emptyList(),
    val categories: List<String> = listOf("All"),
    val selectedCategory: String = "All",
    val query: String = "",
    val loading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BooksUiState())
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun updateSearch(query: String) {
        _uiState.update { it.copy(query = query) }
        load()
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        load()
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            contentRepository.toggleFavorite(id, ContentType.Book)
            load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            val current = uiState.value
            _uiState.update { it.copy(loading = true, error = null) }
            when (val result = contentRepository.getBooks(current.query, current.selectedCategory)) {
                is AppResult.Success -> {
                    val categories = listOf("All") + result.data.map { it.category }.distinct().sorted()
                    _uiState.update { it.copy(books = result.data, categories = categories, loading = false) }
                }
                is AppResult.Error -> _uiState.update { it.copy(error = result.message, loading = false) }
            }
        }
    }
}

