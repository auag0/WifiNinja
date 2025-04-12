package io.github.auag0.wifininja.utils

import android.content.Context
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.util.Base64
import io.github.auag0.wifininja.utils.WifiConfigurationUtils.ssid
import io.github.auag0.wifininja.utils.WifiConfigurationUtils.toByteArray
import io.github.auag0.wifininja.utils.WifiConfigurationUtils.toWifiConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class WifiConfigStorageManager(
    private val appContext: Context
) {
    suspend fun writeWifiConfigs(
        uri: Uri,
        wifiConfigs: List<WifiConfiguration>
    ) {
        val jsonArray = JSONArray()
        wifiConfigs.forEach { wifiConfig ->
            val byteArray = wifiConfig.toByteArray()
            val base64Str = Base64.encodeToString(byteArray, Base64.DEFAULT)
            jsonArray.put(
                JSONObject().apply {
                    put("SSID", wifiConfig.ssid)
                    put("config", base64Str)
                }
            )
        }
        withContext(Dispatchers.IO) {
            appContext.contentResolver.openOutputStream(uri)?.use {
                it.write(jsonArray.toString().toByteArray())
            } ?: throw IllegalStateException("Failed to open output stream")
        }
    }

    suspend fun readWifiConfigs(uri: Uri): List<WifiConfiguration> {
        val wifiConfigs = mutableListOf<WifiConfiguration>()
        withContext(Dispatchers.IO) {
            appContext.contentResolver.openInputStream(uri)?.use {
                val jsonString = it.bufferedReader().readText()
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val base64Str = jsonObject.getString("config")
                    val byteArray = Base64.decode(base64Str, Base64.DEFAULT)
                    val wifiConfig = byteArray.toWifiConfiguration()
                    wifiConfigs.add(wifiConfig)
                }
            } ?: throw IllegalStateException("Failed to open input stream")
        }
        return wifiConfigs.toList()
    }
}