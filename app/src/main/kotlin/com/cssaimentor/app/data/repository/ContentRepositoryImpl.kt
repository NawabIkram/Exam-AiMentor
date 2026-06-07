package com.cssaimentor.app.data.repository

import com.cssaimentor.app.BuildConfig
import com.cssaimentor.app.data.local.dao.FavoriteDao
import com.cssaimentor.app.data.local.entity.FavoriteEntity
import com.cssaimentor.app.data.model.BookDto
import com.cssaimentor.app.data.model.PaperDto
import com.cssaimentor.app.data.model.SampleContent
import com.cssaimentor.app.domain.model.Book
import com.cssaimentor.app.domain.model.ContentType
import com.cssaimentor.app.domain.model.Paper
import com.cssaimentor.app.domain.repository.ContentRepository
import com.cssaimentor.app.utils.AppResult
import com.cssaimentor.app.utils.Constants
import com.cssaimentor.app.utils.awaitTask
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val favoriteDao: FavoriteDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ContentRepository {

    override suspend fun getPapers(query: String, subject: String?): AppResult<List<Paper>> = withContext(ioDispatcher) {
        val favorites = favoriteDao.getFavorites()
            .filter { it.type == ContentType.Paper.name }
            .map { it.itemId }
            .toSet()

        val remote = withTimeoutOrNull(Constants.REMOTE_CONTENT_TIMEOUT_MS) {
            runCatching {
                firestore.collection(Constants.FIRESTORE_PAPERS).get().awaitTask().documents.mapNotNull { doc ->
                    doc.toObject(PaperDto::class.java)?.copy(id = doc.id)?.toDomain(doc.id in favorites)
                }
            }.getOrDefault(emptyList())
        }.orEmpty()

        val source = remote.ifEmpty {
            if (BuildConfig.ALLOW_DEMO_FALLBACK) {
                SampleContent.papers.map { it.copy(isFavorite = it.id in favorites) }
            } else {
                emptyList()
            }
        }
        if (source.isEmpty() && !BuildConfig.ALLOW_DEMO_FALLBACK) {
            AppResult.Error("No papers found in Firestore. Add data to '${Constants.FIRESTORE_PAPERS}'.")
        } else {
            AppResult.Success(source.filterPaper(query, subject))
        }
    }

    override suspend fun getBooks(query: String, category: String?): AppResult<List<Book>> = withContext(ioDispatcher) {
        val favorites = favoriteDao.getFavorites()
            .filter { it.type == ContentType.Book.name }
            .map { it.itemId }
            .toSet()

        val remote = withTimeoutOrNull(Constants.REMOTE_CONTENT_TIMEOUT_MS) {
            runCatching {
                firestore.collection(Constants.FIRESTORE_BOOKS).get().awaitTask().documents.mapNotNull { doc ->
                    doc.toObject(BookDto::class.java)?.copy(id = doc.id)?.toDomain(doc.id in favorites)
                }
            }.getOrDefault(emptyList())
        }.orEmpty()

        val source = remote.ifEmpty {
            if (BuildConfig.ALLOW_DEMO_FALLBACK) {
                SampleContent.books.map { it.copy(isFavorite = it.id in favorites) }
            } else {
                emptyList()
            }
        }
        if (source.isEmpty() && !BuildConfig.ALLOW_DEMO_FALLBACK) {
            AppResult.Error("No books found in Firestore. Add data to '${Constants.FIRESTORE_BOOKS}'.")
        } else {
            AppResult.Success(source.filterBook(query, category))
        }
    }

    override suspend fun toggleFavorite(itemId: String, type: ContentType) = withContext(ioDispatcher) {
        val existing = favoriteDao.getFavorite(itemId)
        if (existing == null) {
            favoriteDao.insert(FavoriteEntity(itemId = itemId, type = type.name))
        } else {
            favoriteDao.delete(existing)
        }
    }

    private fun List<Paper>.filterPaper(query: String, subject: String?) = filter { paper ->
        val matchesQuery = query.isBlank() ||
            paper.title.contains(query, true) ||
            paper.subject.contains(query, true) ||
            paper.year.toString().contains(query)
        val matchesSubject = subject == null || subject == "All" || paper.subject == subject
        matchesQuery && matchesSubject
    }.sortedWith(compareByDescending<Paper> { it.year }.thenBy { it.subject })

    private fun List<Book>.filterBook(query: String, category: String?) = filter { book ->
        val matchesQuery = query.isBlank() ||
            book.title.contains(query, true) ||
            book.author.contains(query, true) ||
            book.category.contains(query, true)
        val matchesCategory = category == null || category == "All" || book.category == category
        matchesQuery && matchesCategory
    }.sortedBy { it.title }
}
