package com.example.giphychili.data.giphy.paging


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.giphychili.data.giphy.remote.GiphyApi
import com.example.giphychili.data.giphy.mapper.toEntity
import com.example.giphychili.domain.giphy.entity.Gif

class GiphyPagingSource(
    private val api: GiphyApi,
    private val apiKey: String,
    private val query: String,
    private val pageSize: Int
) : PagingSource<Int, Gif>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Gif> {
        val offset = params.key ?: 0
        return try {
            val res = api.search(query, apiKey, limit = pageSize, offset = offset)
            val items = res.data.map { it.toEntity() }
            val nextOffset = if (items.isEmpty()) null else offset + res.pagination.count
            LoadResult.Page(
                data = items,
                prevKey = if (offset == 0) null else (offset - pageSize).coerceAtLeast(0),
                nextKey = nextOffset
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Gif>): Int? =
        state.anchorPosition?.let { pos ->
            val page = state.closestPageToPosition(pos)
            page?.prevKey?.plus(pageSize) ?: page?.nextKey?.minus(pageSize)
        }
}
