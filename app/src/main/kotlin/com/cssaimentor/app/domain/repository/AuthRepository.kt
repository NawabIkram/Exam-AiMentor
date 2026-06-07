package com.cssaimentor.app.domain.repository

import com.cssaimentor.app.domain.model.UserProfile
import com.cssaimentor.app.utils.AppResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<UserProfile?>
    suspend fun login(email: String, password: String): AppResult<UserProfile>
    suspend fun signup(name: String, email: String, password: String): AppResult<UserProfile>
    suspend fun sendPasswordReset(email: String): AppResult<Unit>
    suspend fun signInWithGoogle(idToken: String): AppResult<UserProfile>
    suspend fun logout()
}

