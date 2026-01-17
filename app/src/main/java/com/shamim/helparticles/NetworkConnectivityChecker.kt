package com.shamim.helparticles

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

interface NetworkConnectivityChecker {
    fun isOnline(): Boolean
}

class NetworkConnectivityCheckerImpl(
    private val connectivityManager: ConnectivityManager
): NetworkConnectivityChecker {
    override fun isOnline(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}