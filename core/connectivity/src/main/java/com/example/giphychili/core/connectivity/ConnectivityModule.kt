package com.example.giphychili.core.connectivity

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConnectivityModule {
    @Provides @Singleton
    fun provideConnectivity(obs: ConnectivityObserverImpl): ConnectivityObserver = obs
}