package com.example.giphychili.domain.giphy

import androidx.paging.Pager
import com.example.giphychili.domain.giphy.entity.Gif

interface GiphyRepository {
    fun searchPaged(query: String, pageSize: Int): Pager<Int, Gif>
}