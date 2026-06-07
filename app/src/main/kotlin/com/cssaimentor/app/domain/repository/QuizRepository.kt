package com.cssaimentor.app.domain.repository

import com.cssaimentor.app.domain.model.QuizQuestion
import com.cssaimentor.app.domain.model.QuizResult
import com.cssaimentor.app.domain.model.QuizTopic
import com.cssaimentor.app.utils.AppResult

interface QuizRepository {
    suspend fun getTopics(): AppResult<List<QuizTopic>>
    suspend fun getQuestions(topicId: String): AppResult<List<QuizQuestion>>
    suspend fun saveResult(result: QuizResult): AppResult<Unit>
}

