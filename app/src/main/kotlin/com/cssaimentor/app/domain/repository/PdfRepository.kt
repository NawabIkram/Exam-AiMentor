package com.cssaimentor.app.domain.repository

import com.cssaimentor.app.utils.AppResult
import java.io.File

interface PdfRepository {
    suspend fun downloadPdf(documentId: String, title: String, url: String): AppResult<File>
    suspend fun saveBookmark(documentId: String, pageIndex: Int)
    suspend fun getBookmark(documentId: String): Int?
}

