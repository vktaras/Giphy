package com.example.giphychili.di

import com.example.giphychili.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    @Singleton
    @Named("GiphyApiKey")
    fun provideGiphyApiKey(): String = BuildConfig.GIPHY_API_KEY
}