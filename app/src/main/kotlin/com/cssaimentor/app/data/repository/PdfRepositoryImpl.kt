package com.cssaimentor.app.data.repository

import android.content.Context
import com.cssaimentor.app.data.local.dao.PdfBookmarkDao
import com.cssaimentor.app.data.local.entity.PdfBookmarkEntity
import com.cssaimentor.app.domain.repository.PdfRepository
import com.cssaimentor.app.utils.AppResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val bookmarkDao: PdfBookmarkDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PdfRepository {

    override suspend fun downloadPdf(documentId: String, title: String, url: String): AppResult<File> = withContext(ioDispatcher) {
        runCatching {
            val cacheDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
            val file = File(cacheDir, "${documentId.safeName()}-${title.safeName()}.pdf")
            if (file.exists() && file.length() > 0) return@runCatching file

            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) error("PDF download failed: ${response.code}")
                val body = response.body ?: error("Empty PDF response")
                file.outputStream().use { output ->
                    body.byteStream().use { input -> input.copyTo(output) }
                }
            }
            file
        }.fold(
            onSuccess = { AppResult.Success(it) },
            onFailure = { AppResult.Error("Unable to open PDF. Check internet connection.", it) }
        )
    }

    override suspend fun saveBookmark(documentId: String, pageIndex: Int) = withContext(ioDispatcher) {
        bookmarkDao.upsert(PdfBookmarkEntity(documentId = documentId, pageIndex = pageIndex))
    }

    override suspend fun getBookmark(documentId: String): Int? = withContext(ioDispatcher) {
        bookmarkDao.getBookmark(documentId)
    }

    private fun String.safeName(): String = lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
        .ifBlank { "document" }
}

