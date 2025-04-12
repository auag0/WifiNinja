package io.github.auag0.wifininja.repository

import android.net.wifi.WifiConfiguration
import io.github.auag0.wifininja.IWifiService
import io.github.auag0.wifininja.models.WifiConfigurationWrapper
import io.github.auag0.wifininja.services.WifiServiceManager
import javax.inject.Inject

class WifiRepository @Inject constructor(
    private val wifiServiceManager: WifiServiceManager
) {
    private val wifiService: IWifiService
        get() = wifiServiceManager.requireService()

    suspend fun ensureWifiService(): Boolean {
        return wifiServiceManager.ensureService()
    }

    fun unbind() {
        wifiServiceManager.unbind()
    }

    fun getConfiguredNetworks(): List<WifiConfiguration> {
        return wifiService.configuredNetworks.mapNotNull {
            it.value
        }
    }

    fun addNetwork(wifiConfig: WifiConfiguration): Int {
        val networkId = wifiService.addNetwork(WifiConfigurationWrapper(wifiConfig))
        return networkId
    }
}