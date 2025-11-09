package com.example.giphychili.core.common

sealed interface AppError {
    data class Network(val code: Int?, val message: String?) : AppError
    data class Unknown(val cause: Throwable?) : AppError
}