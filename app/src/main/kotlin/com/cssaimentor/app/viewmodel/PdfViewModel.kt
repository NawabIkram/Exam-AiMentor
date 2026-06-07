package com.cssaimentor.app.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cssaimentor.app.domain.repository.PdfRepository
import com.cssaimentor.app.utils.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class PdfUiState(
    val documentId: String = "",
    val title: String = "",
    val url: String = "",
    val file: File? = null,
    val loading: Boolean = true,
    val error: String? = null,
    val bookmarkPage: Int? = null,
    val zoom: Float = 1f
)

@HiltViewModel
class PdfViewModel @Inject constructor(
    private val pdfRepository: PdfRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val documentId = Uri.decode(savedStateHandle["documentId"] ?: "")
    private val title = Uri.decode(savedStateHandle["title"] ?: "PDF")
    private val url = Uri.decode(savedStateHandle["url"] ?: "")

    private val _uiState = MutableStateFlow(PdfUiState(documentId = documentId, title = title, url = url))
    val uiState: StateFlow<PdfUiState> = _uiState.asStateFlow()

    init {
        open()
    }

    fun open() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val bookmark = pdfRepository.getBookmark(documentId)
            when (val result = pdfRepository.downloadPdf(documentId, title, url)) {
                is AppResult.Success -> _uiState.update { it.copy(file = result.data, loading = false, bookmarkPage = bookmark) }
                is AppResult.Error -> _uiState.update { it.copy(error = result.message, loading = false) }
            }
        }
    }

    fun updateZoom(value: Float) {
        _uiState.update { it.copy(zoom = value.coerceIn(0.75f, 2.25f)) }
    }

    fun bookmark(pageIndex: Int) {
        viewModelScope.launch {
            pdfRepository.saveBookmark(documentId, pageIndex)
            _uiState.update { it.copy(bookmarkPage = pageIndex) }
        }
    }
}

