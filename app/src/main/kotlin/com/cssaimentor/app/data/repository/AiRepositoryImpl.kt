package com.cssaimentor.app.data.repository

import com.cssaimentor.app.BuildConfig
import com.cssaimentor.app.data.local.dao.ChatDao
import com.cssaimentor.app.data.local.entity.ChatEntity
import com.cssaimentor.app.data.remote.GeminiApi
import com.cssaimentor.app.data.remote.GeminiContent
import com.cssaimentor.app.data.remote.GeminiPart
import com.cssaimentor.app.data.remote.GeminiRequest
import com.cssaimentor.app.domain.model.ChatMessage
import com.cssaimentor.app.domain.model.ChatRole
import com.cssaimentor.app.domain.repository.AiRepository
import com.cssaimentor.app.utils.AppResult
import com.cssaimentor.app.utils.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val geminiApi: GeminiApi,
    private val chatDao: ChatDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AiRepository {

    override fun observeChat(): Flow<List<ChatMessage>> = chatDao.observeMessages().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun sendMessage(message: String): AppResult<ChatMessage> = withContext(ioDispatcher) {
        val userEntity = ChatEntity(
            id = UUID.randomUUID().toString(),
            role = ChatRole.User.name,
            text = message.trim(),
            createdAt = System.currentTimeMillis()
        )
        chatDao.insert(userEntity)
        generateAndPersist(message.trim())
    }

    override suspend fun regenerateLastResponse(): AppResult<ChatMessage> = withContext(ioDispatcher) {
        val latest = chatDao.latestUserMessage()
        if (latest == null) {
            AppResult.Error("Ask a question first.")
        } else {
            generateAndPersist(latest.text)
        }
    }

    override suspend fun clearChat() {
        chatDao.clear()
    }

    private suspend fun generateAndPersist(userPrompt: String): AppResult<ChatMessage> {
        val hasGeminiKey = BuildConfig.GEMINI_API_KEY.isNotBlank()
        val text = runCatching {
            if (!hasGeminiKey) {
                if (BuildConfig.ALLOW_DEMO_FALLBACK) {
                    delay(900)
                    demoMentorResponse(userPrompt)
                } else {
                    error("Gemini API key is missing. Set GEMINI_API_KEY.")
                }
            } else {
                geminiApi.generateContent(
                    model = Constants.GEMINI_MODEL,
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = GeminiRequest(
                        systemInstruction = GeminiContent(
                            parts = listOf(GeminiPart(CSS_MENTOR_SYSTEM_PROMPT))
                        ),
                        contents = listOf(
                            GeminiContent(
                                role = "user",
                                parts = listOf(GeminiPart(userPrompt))
                            )
                        )
                    )
                ).bestText().ifBlank {
                    error("Gemini returned an empty response.")
                }
            }
        }.getOrElse {
            if (!hasGeminiKey && BuildConfig.ALLOW_DEMO_FALLBACK) {
                demoMentorResponse(userPrompt)
            } else {
                return AppResult.Error("AI request failed. Check Gemini key and network.", it)
            }
        }

        val response = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = ChatRole.Mentor,
            text = text,
            createdAt = System.currentTimeMillis()
        )
        chatDao.insert(response.toEntity())
        return AppResult.Success(response)
    }

    private fun ChatEntity.toDomain() = ChatMessage(
        id = id,
        role = if (role == ChatRole.User.name) ChatRole.User else ChatRole.Mentor,
        text = text,
        createdAt = createdAt
    )

    private fun ChatMessage.toEntity() = ChatEntity(
        id = id,
        role = role.name,
        text = text,
        createdAt = createdAt
    )

    private fun demoMentorResponse(prompt: String): String {
        val focus = prompt.take(80).ifBlank { "CSS preparation" }
        return """
            ## Smart CSS Plan
            
            For **$focus**, use a three-layer approach:
            
            1. Build a one-page concept map.
            2. Add 5 CSS-style examples from Pakistan, governance, economy, or global affairs.
            3. Practice one short answer and one MCQ set immediately.
            
            **Mentor tip:** write answers in examiner-friendly structure: introduction, 3-5 analytical headings, evidence, and a balanced conclusion.
            
            Ask me to generate MCQs, an outline, or a 7-day study plan for this topic.
        """.trimIndent()
    }

    private companion object {
        const val CSS_MENTOR_SYSTEM_PROMPT = """
            You are CSS AI Mentor, a premium exam preparation assistant for Pakistan CSS aspirants.
            
            App identity and context:
            - Product name: CSS AI Mentor.
            - Product owner/developer: Nawab Ikram.
            - Mission: help Pakistani CSS aspirants prepare with AI guidance, past papers, MCQs, books, notes, and smart study tools.
            - Current MVP modules: AI Mentor chat, Home Dashboard, Past Papers, Books Library, PDF Viewer, MCQ Quiz, Profile, Firebase-ready auth/data, and local Room storage.
            - Database content may still be configured later, so explain app capabilities honestly if asked.
            
            Be precise, exam-oriented, structured, and honest. Prefer Pakistan-relevant examples.
            Use markdown headings, bullets, tables when helpful, and practical study actions.
            Never invent current facts. If unsure, say what to verify.
        """
    }
}
