package com.example.giphychili.data.giphy.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.giphychili.data.giphy.paging.GiphyPagingSource
import com.example.giphychili.data.giphy.remote.GiphyApi
import com.example.giphychili.domain.giphy.GiphyRepository
import com.example.giphychili.domain.giphy.entity.Gif
import javax.inject.Inject

class GiphyRepositoryImpl @Inject constructor(
    private val api: GiphyApi,
    private val apiKey: String
) : GiphyRepository {

    override fun searchPaged(query: String, pageSize: Int): Pager<Int, Gif> =
        Pager(
            config = PagingConfig(pageSize = pageSize, initialLoadSize = pageSize)
        ) {
            GiphyPagingSource(api, apiKey, query, pageSize)
        }
}