package com.example.giphychili.di

import com.example.giphychili.domain.giphy.GiphyRepository
import com.example.giphychili.domain.giphy.usecase.SearchGifsPagedUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    fun provideSearchGifsPagedUseCase(
        repo: GiphyRepository
    ): SearchGifsPagedUseCase = SearchGifsPagedUseCase(repo)
}