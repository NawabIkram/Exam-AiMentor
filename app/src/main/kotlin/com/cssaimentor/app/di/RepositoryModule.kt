package com.cssaimentor.app.di

import com.cssaimentor.app.data.repository.AiRepositoryImpl
import com.cssaimentor.app.data.repository.AuthRepositoryImpl
import com.cssaimentor.app.data.repository.ContentRepositoryImpl
import com.cssaimentor.app.data.repository.PdfRepositoryImpl
import com.cssaimentor.app.data.repository.QuizRepositoryImpl
import com.cssaimentor.app.data.repository.UserPreferencesRepositoryImpl
import com.cssaimentor.app.domain.repository.AiRepository
import com.cssaimentor.app.domain.repository.AuthRepository
import com.cssaimentor.app.domain.repository.ContentRepository
import com.cssaimentor.app.domain.repository.PdfRepository
import com.cssaimentor.app.domain.repository.QuizRepository
import com.cssaimentor.app.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(impl: AiRepositoryImpl): AiRepository

    @Binds
    @Singleton
    abstract fun bindContentRepository(impl: ContentRepositoryImpl): ContentRepository

    @Binds
    @Singleton
    abstract fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository

    @Binds
    @Singleton
    abstract fun bindPdfRepository(impl: PdfRepositoryImpl): PdfRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}

