package com.cssaimentor.app.utils

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        when {
            task.isSuccessful -> continuation.resume(task.result)
            task.exception != null -> continuation.resumeWithException(task.exception!!)
            else -> continuation.resumeWithException(IllegalStateException("Firebase task was cancelled"))
        }
    }
}

