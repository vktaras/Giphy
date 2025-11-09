package com.example.giphychili.core.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class ConnectivityObserverImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ConnectivityObserver {

    override val status = callbackFlow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val cb = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(true).isSuccess }
            override fun onLost(network: Network) { trySend(false).isSuccess }
        }
        cm.registerNetworkCallback(NetworkRequest.Builder().build(), cb)
        trySend(isOnline(cm))
        awaitClose { cm.unregisterNetworkCallback(cb) }
    }.distinctUntilChanged()

    private fun isOnline(cm: ConnectivityManager): Boolean =
        cm.activeNetwork?.let { n ->
            cm.getNetworkCapabilities(n)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } == true
}
