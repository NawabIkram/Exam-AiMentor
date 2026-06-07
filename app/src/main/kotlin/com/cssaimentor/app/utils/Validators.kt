package com.cssaimentor.app.utils

import android.util.Patterns

object Validators {
    fun emailError(email: String): String? = when {
        email.isBlank() -> "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email"
        else -> null
    }

    fun passwordError(password: String): String? = when {
        password.isBlank() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters"
        else -> null
    }

    fun nameError(name: String): String? = when {
        name.trim().length < 2 -> "Enter your full name"
        else -> null
    }
}

