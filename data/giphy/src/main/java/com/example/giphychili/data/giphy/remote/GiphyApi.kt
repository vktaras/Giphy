package com.example.giphychili.data.giphy.remote

import com.example.giphychili.data.giphy.remote.dto.GiphySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyApi {
    @GET("v1/gifs/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("rating") rating: String = "g"
    ): GiphySearchResponse
}