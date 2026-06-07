package com.cssaimentor.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cssaimentor.app.domain.model.DashboardStats
import com.cssaimentor.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {
    val stats: StateFlow<DashboardStats> = authRepository.currentUser.map { user ->
        DashboardStats(
            greetingName = user?.name ?: "CSS Aspirant",
            streak = user?.streak ?: 7,
            weeklyProgress = user?.progress ?: 0.42f,
            studyMinutes = user?.totalStudyMinutes ?: 480,
            quizAccuracy = 0.78f
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        DashboardStats("CSS Aspirant", 7, 0.42f, 480, 0.78f)
    )
}

