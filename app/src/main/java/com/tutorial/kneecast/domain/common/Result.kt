package com.tutorial.kneecast.domain.common

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    // Consider adding a Loading state if needed for UIs:
    // object Loading : Result<Nothing>()
}
