package com.cssaimentor.app.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GeminiApi {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GeminiGenerationConfig = GeminiGenerationConfig()
)

data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

data class GeminiPart(
    val text: String
)

data class GeminiGenerationConfig(
    val temperature: Double = 0.55,
    val topP: Double = 0.9,
    val maxOutputTokens: Int = 900
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate> = emptyList()
) {
    fun bestText(): String = candidates.firstOrNull()
        ?.content
        ?.parts
        ?.joinToString("\n") { it.text }
        .orEmpty()
}

data class GeminiCandidate(
    val content: GeminiContent = GeminiContent(emptyList())
)
