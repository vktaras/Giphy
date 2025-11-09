package com.example.giphychili.domain.giphy.entity

data class Gif(
    val id: String,
    val title: String?,
    val previewUrl: String,
    val originalUrl: String,
    val width: Int?,
    val height: Int?
)