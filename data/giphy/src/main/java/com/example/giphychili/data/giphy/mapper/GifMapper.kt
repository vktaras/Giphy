package com.example.giphychili.data.giphy.mapper

import com.example.giphychili.data.giphy.remote.dto.GifDto
import com.example.giphychili.domain.giphy.entity.Gif

fun GifDto.toEntity(): Gif {
    val preview = images.fixed_width?.url ?: images.downsized?.url ?: images.original?.url.orEmpty()
    val original = images.original?.url ?: preview
    val w = images.original?.width ?: images.fixed_width?.width
    val h = images.original?.height ?: images.fixed_width?.height
    return Gif(
        id = id,
        title = title,
        previewUrl = preview,
        originalUrl = original,
        width = w?.toIntOrNull(),
        height = h?.toIntOrNull()
    )
}