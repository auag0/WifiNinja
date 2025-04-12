package io.github.auag0.wifininja.models

import android.net.wifi.WifiConfiguration
import io.github.auag0.wifininja.utils.WifiConfigurationUtils.password
import io.github.auag0.wifininja.utils.WifiConfigurationUtils.ssid
import java.io.Serializable
import java.util.BitSet

data class WifiItem(
    val ssid: String,
    val password: String,
    val networkId: Int,
    val hiddenSSID: Boolean,
    val allowedKeyManagement: BitSet
) : Serializable {
    companion object {
        fun from(wifiConfig: WifiConfiguration): WifiItem {
            return WifiItem(
                ssid = wifiConfig.ssid,
                password = wifiConfig.password,
                networkId = wifiConfig.networkId,
                hiddenSSID = wifiConfig.hiddenSSID,
                allowedKeyManagement = wifiConfig.allowedKeyManagement
            )
        }
    }
}