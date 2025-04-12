package io.github.auag0.wifininja.utils

import android.annotation.SuppressLint
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiConfiguration.AuthAlgorithm
import android.net.wifi.WifiConfiguration.KeyMgmt
import android.os.Parcel
import android.os.Parcelable
import io.github.auag0.wifininja.utils.StringUtils.unquote

object WifiConfigurationUtils {
    fun WifiConfiguration.toByteArray(): ByteArray {
        val parcel = Parcel.obtain()
        this.writeToParcel(parcel, 0)
        val byteArray = parcel.marshall()
        parcel.recycle()
        return byteArray
    }

    fun ByteArray.toWifiConfiguration(): WifiConfiguration {
        val wifiConfigurationClass = Class.forName("android.net.wifi.WifiConfiguration")
        val creator = wifiConfigurationClass.getField("CREATOR").apply {
            isAccessible = true
        }.get(null) as Parcelable.Creator<*>
        val parcel = Parcel.obtain()
        parcel.unmarshall(this, 0, this.size)
        parcel.setDataPosition(0)
        val wifiConfiguration = creator.createFromParcel(parcel) as WifiConfiguration
        parcel.recycle()
        return wifiConfiguration
    }

    @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    fun Parcel.readWifiConfiguration(): WifiConfiguration {
        val wifiConfigurationClass = Class.forName("android.net.wifi.WifiConfiguration")
        val creator = wifiConfigurationClass.getDeclaredField("CREATOR").apply {
            isAccessible = true
        }.get(null) as Parcelable.Creator<*>
        return creator.createFromParcel(this) as WifiConfiguration
    }

    val WifiConfiguration.ssid: String
        get() = SSID.unquote()

    val WifiConfiguration.password: String
        get() {
            val password = if (
                allowedKeyManagement.get(KeyMgmt.NONE) &&
                allowedAuthAlgorithms.get(AuthAlgorithm.SHARED)
            ) {
                wepKeys.getOrNull(wepTxKeyIndex)
            } else {
                preSharedKey
            }
            return password?.unquote() ?: ""
        }
}