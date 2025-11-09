package com.example.giphychili.data.giphy.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GiphySearchResponse(
    val data: List<GifDto>,
    val pagination: PaginationDto
)

@Serializable
data class GifDto(
    val id: String,
    val title: String? = null,
    val images: ImagesDto
)

@Serializable
data class ImagesDto(
    val downsized: ImageRendition? = null,
    val fixed_width: ImageRendition? = null,
    val original: ImageRendition? = null
)

@Serializable
data class ImageRendition(
    val url: String,
    val width: String? = null,
    val height: String? = null
)

@Serializable
data class PaginationDto(
    val total_count: Int,
    val count: Int,
    val offset: Int
)
