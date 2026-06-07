package com.cssaimentor.app.domain.repository

import com.cssaimentor.app.domain.model.Book
import com.cssaimentor.app.domain.model.ContentType
import com.cssaimentor.app.domain.model.Paper
import com.cssaimentor.app.utils.AppResult

interface ContentRepository {
    suspend fun getPapers(query: String = "", subject: String? = null): AppResult<List<Paper>>
    suspend fun getBooks(query: String = "", category: String? = null): AppResult<List<Book>>
    suspend fun toggleFavorite(itemId: String, type: ContentType)
}

