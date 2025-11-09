package com.example.giphychili.domain.giphy.usecase

import com.example.giphychili.domain.giphy.GiphyRepository


class SearchGifsPagedUseCase(private val repo: GiphyRepository) {
    operator fun invoke(query: String, pageSize: Int) = repo.searchPaged(query, pageSize)
}