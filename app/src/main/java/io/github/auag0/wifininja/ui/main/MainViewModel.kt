package io.github.auag0.wifininja.ui.main

import android.app.Application
import android.net.Uri
import android.net.wifi.WifiConfiguration
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.auag0.wifininja.R
import io.github.auag0.wifininja.exception.RootRequiredException
import io.github.auag0.wifininja.models.WifiItem
import io.github.auag0.wifininja.models.toast.ToastMessage
import io.github.auag0.wifininja.models.toast.ToastMessage.ResId
import io.github.auag0.wifininja.models.toast.ToastMessage.Text
import io.github.auag0.wifininja.repository.WifiRepository
import io.github.auag0.wifininja.utils.WifiConfigStorageManager
import io.github.auag0.wifininja.utils.WifiConfigurationUtils.ssid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    app: Application,
    private val wifiRepository: WifiRepository,
    private val wifiConfigStorageManager: WifiConfigStorageManager
) : AndroidViewModel(app) {
    private var configuredNetworks = listOf<WifiConfiguration>()

    private val _wifiList = MutableStateFlow(emptyList<WifiItem>())
    val wifiList = _wifiList.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _toastMessage = MutableSharedFlow<ToastMessage>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val _restoredConfigs = MutableStateFlow(emptyList<WifiConfiguration>())
    val restoredConfigs = _restoredConfigs.asStateFlow()

    private val _isLoadingRestoredConfigs = MutableStateFlow(false)
    val isLoadingRestoredConfigs = _isLoadingRestoredConfigs.asStateFlow()

    private var backupNetworkIds = emptyList<Int>()

    init {
        loadWifiList()
    }

    private suspend fun sendToast(message: ToastMessage) {
        _toastMessage.emit(message)
    }

    fun loadWifiList() {
        if (_isRefreshing.value) return

        viewModelScope.launch(Dispatchers.Default) {
            _isRefreshing.emit(true)
            try {
                if (!wifiRepository.ensureWifiService()) {
                    throw IllegalStateException("Failed to bind WifiService")
                }
                configuredNetworks = wifiRepository.getConfiguredNetworks()
                val wifiList = configuredNetworks.map { WifiItem.from(it) }
                _wifiList.emit(wifiList)
            } catch (_: RootRequiredException) {
                sendToast(ResId(R.string.root_required))
            } catch (e: Throwable) {
                e.printStackTrace()
                sendToast(Text(e.message ?: "Unknown error"))
            } finally {
                _isRefreshing.emit(false)
            }
        }
    }

    private fun getWifiConfig(networkId: Int): WifiConfiguration? {
        return configuredNetworks.find { it.networkId == networkId }
    }

    override fun onCleared() {
        super.onCleared()
        wifiRepository.unbind()
    }

    fun updateSelectedNetworkIds(selectedNetworkIds: List<Int>) {
        backupNetworkIds = selectedNetworkIds
    }

    fun handleBackup(uri: Uri) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val wifiConfigs = backupNetworkIds.distinct().map { networkId ->
                    getWifiConfig(networkId) ?: throw IllegalStateException("Network not found")
                }
                wifiConfigStorageManager.writeWifiConfigs(uri, wifiConfigs)
                sendToast(ResId(R.string.backup_success))
            } catch (e: Exception) {
                e.printStackTrace()
                sendToast(Text(e.message ?: "Unknown error"))
            } finally {
                backupNetworkIds = emptyList()
            }
        }
    }

    fun loadRestoredConfigs(uri: Uri) {
        viewModelScope.launch(Dispatchers.Default) {
            _isLoadingRestoredConfigs.emit(true)
            try {
                val wifiConfigs = wifiConfigStorageManager.readWifiConfigs(uri)
                _restoredConfigs.emit(wifiConfigs)
            } catch (e: Exception) {
                e.printStackTrace()
                sendToast(Text(e.message ?: "Unknown error"))
                _restoredConfigs.emit(emptyList())
            } finally {
                _isLoadingRestoredConfigs.emit(false)
            }
        }
    }

    fun handleRestoreSelected(selectedSSIDs: List<String>) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val selectedWifiConfigs = _restoredConfigs.value.filter {
                    selectedSSIDs.contains(it.ssid)
                }
                var successCount = 0
                var failCount = 0
                selectedWifiConfigs.forEach { wifiConfig ->
                    val networkId = wifiRepository.addNetwork(wifiConfig)
                    if (networkId == -1) {
                        failCount++
                    } else {
                        successCount++
                    }
                }
                if (failCount > 0) {
                    sendToast(
                        ResId(
                            R.string.restore_partial_success,
                            arrayOf(successCount, failCount)
                        )
                    )
                } else {
                    sendToast(
                        ResId(
                            R.string.restore_success,
                            arrayOf(successCount)
                        )
                    )
                }
                _restoredConfigs.emit(emptyList())
                loadWifiList()
            } catch (e: Exception) {
                e.printStackTrace()
                sendToast(Text(e.message ?: "Unknown error"))
            }
        }
    }
}