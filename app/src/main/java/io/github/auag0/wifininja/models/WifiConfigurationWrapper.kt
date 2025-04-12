package io.github.auag0.wifininja.models

import android.net.wifi.WifiConfiguration
import android.os.Parcel
import android.os.Parcelable
import io.github.auag0.wifininja.utils.WifiConfigurationUtils.readWifiConfiguration

data class WifiConfigurationWrapper(
    val value: WifiConfiguration
) : Parcelable {
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<WifiConfigurationWrapper> {
            override fun createFromParcel(source: Parcel): WifiConfigurationWrapper {
                return WifiConfigurationWrapper(
                    value = source.readWifiConfiguration()
                )
            }

            override fun newArray(size: Int): Array<out WifiConfigurationWrapper?>? {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        value.writeToParcel(dest, flags)
    }

    override fun describeContents(): Int {
        return 0
    }
}