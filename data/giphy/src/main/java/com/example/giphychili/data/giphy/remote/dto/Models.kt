package com.example.giphychili.data.giphy.remote.dto

data class GiphySearchResponse(
    val data: List<GifDto>,
    val pagination: PaginationDto
)

data class GifDto(
    val id: String,
    val title: String? = null,
    val images: ImagesDto
)

data class ImagesDto(
    val downsized: ImageRendition? = null,
    val fixed_width: ImageRendition? = null,
    val original: ImageRendition? = null
)

data class ImageRendition(
    val url: String,
    val width: String? = null,
    val height: String? = null
)

data class PaginationDto(
    val total_count: Int,
    val count: Int,
    val offset: Int
)
