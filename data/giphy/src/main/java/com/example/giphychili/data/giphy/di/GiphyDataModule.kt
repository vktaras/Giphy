package com.example.giphychili.data.giphy.di

import com.example.giphychili.data.giphy.remote.GiphyApi
import com.example.giphychili.data.giphy.repository.GiphyRepositoryImpl
import com.example.giphychili.domain.giphy.GiphyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GiphyDataModule {

    @Provides
    @Singleton
    fun provideGiphyApi(retrofit: Retrofit): GiphyApi =
        retrofit.create(GiphyApi::class.java)

    @Provides
    @Singleton
    fun provideGiphyRepository(
        api: GiphyApi,
        @Named("GiphyApiKey") apiKey: String
    ): GiphyRepository =
        GiphyRepositoryImpl(api, apiKey)
}