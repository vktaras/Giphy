package com.example.giphychili.core.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val status: Flow<Boolean>
}