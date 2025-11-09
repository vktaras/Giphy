package com.example.giphychili.core.common

// hasn't been used for current Chili imp,
// but can be used in future while extending and adding new use cases GetGifById, GetTrendingGifs, UploadGif
sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val error: AppError) : AppResult<Nothing>()
}