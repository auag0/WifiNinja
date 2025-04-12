package io.github.auag0.wifininja.services

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import io.github.auag0.wifininja.IWifiService
import io.github.auag0.wifininja.models.WifiConfigurationWrapper
import io.github.auag0.wifininja.utils.AppUtils.getAppContext

class WifiServiceImpl : IWifiService.Stub() {
    private val appContext: Context
        get() = getAppContext().applicationContext

    override fun getConfiguredNetworks(): List<WifiConfigurationWrapper> {
        val wifiManager = appContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiManagerClass = Class.forName("android.net.wifi.WifiManager")
        val getPrivilegedConfiguredNetworks =
            wifiManagerClass.getMethod("getPrivilegedConfiguredNetworks")
        val privilegedConfiguredNetworks =
            getPrivilegedConfiguredNetworks.invoke(wifiManager) as List<*>
        val result = privilegedConfiguredNetworks.filterIsInstance<WifiConfiguration>()
            .distinctBy { it.networkId }
            .map { WifiConfigurationWrapper(it) }
        return result
    }

    override fun addNetwork(wifiConfigWrapper: WifiConfigurationWrapper): Int {
        val wifiConfig = wifiConfigWrapper.value
        val wifiManager = appContext.getSystemService(WIFI_SERVICE) as WifiManager
        val networkId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val res = wifiManager.addNetworkPrivileged(wifiConfig)
            res.networkId
        } else {
            wifiManager.addNetwork(wifiConfig)
        }
        return networkId
    }
}