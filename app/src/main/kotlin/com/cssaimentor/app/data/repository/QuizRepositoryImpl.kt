package com.cssaimentor.app.data.repository

import com.cssaimentor.app.BuildConfig
import com.cssaimentor.app.data.model.QuizQuestionDto
import com.cssaimentor.app.data.model.SampleContent
import com.cssaimentor.app.domain.model.QuizQuestion
import com.cssaimentor.app.domain.model.QuizResult
import com.cssaimentor.app.domain.model.QuizTopic
import com.cssaimentor.app.domain.repository.QuizRepository
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
class QuizRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : QuizRepository {

    override suspend fun getTopics(): AppResult<List<QuizTopic>> = withContext(ioDispatcher) {
        if (BuildConfig.ALLOW_DEMO_FALLBACK) {
            AppResult.Success(SampleContent.topics)
        } else {
            val remoteTopicIds = withTimeoutOrNull(Constants.REMOTE_CONTENT_TIMEOUT_MS) {
                runCatching {
                    firestore.collection(Constants.FIRESTORE_QUIZZES)
                        .get()
                        .awaitTask()
                        .documents
                        .mapNotNull { it.getString("topicId") }
                        .distinct()
                }.getOrDefault(emptyList())
            }.orEmpty()

            if (remoteTopicIds.isEmpty()) {
                AppResult.Error("No quiz data found in Firestore. Add documents to '${Constants.FIRESTORE_QUIZZES}'.")
            } else {
                val topics = remoteTopicIds.map { topicId ->
                    val fallbackTopic = SampleContent.topics.firstOrNull { it.id == topicId }
                    fallbackTopic ?: QuizTopic(
                        id = topicId,
                        title = topicId.replaceFirstChar { it.uppercase() },
                        subtitle = "Quiz topic",
                        questionCount = 0,
                        accent = 0xFF7CE7FF
                    )
                }
                AppResult.Success(topics)
            }
        }
    }

    override suspend fun getQuestions(topicId: String): AppResult<List<QuizQuestion>> = withContext(ioDispatcher) {
        val remote = withTimeoutOrNull(Constants.REMOTE_CONTENT_TIMEOUT_MS) {
            runCatching {
                firestore.collection(Constants.FIRESTORE_QUIZZES)
                    .whereEqualTo("topicId", topicId)
                    .get()
                    .awaitTask()
                    .documents
                    .mapNotNull { doc -> doc.toObject(QuizQuestionDto::class.java)?.copy(id = doc.id)?.toDomain() }
            }.getOrDefault(emptyList())
        }.orEmpty()

        val source = remote.ifEmpty {
            if (BuildConfig.ALLOW_DEMO_FALLBACK) {
                SampleContent.questions.filter { it.topicId == topicId }
            } else {
                emptyList()
            }
        }
        if (source.isEmpty() && !BuildConfig.ALLOW_DEMO_FALLBACK) {
            AppResult.Error("No questions found for topic '$topicId' in Firestore.")
        } else {
            AppResult.Success(source)
        }
    }

    override suspend fun saveResult(result: QuizResult): AppResult<Unit> = withContext(ioDispatcher) {
        withTimeoutOrNull(Constants.REMOTE_CONTENT_TIMEOUT_MS) {
            runCatching {
                firestore.collection(Constants.FIRESTORE_RESULTS).add(result).awaitTask()
            }
        }
        AppResult.Success(Unit)
    }
}
