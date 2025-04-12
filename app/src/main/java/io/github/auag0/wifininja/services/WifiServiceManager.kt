package io.github.auag0.wifininja.services

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.auag0.wifininja.IWifiService
import io.github.auag0.wifininja.services.d.RootServiceInterface
import io.github.auag0.wifininja.services.d.ServiceInterface
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class WifiServiceManager(
    @ApplicationContext context: Context
) {
    private val appContext: Context = context.applicationContext

    private var connection: ServiceConnection? = null
    private var iWifiService: IWifiService? = null
    private val isAlive: Boolean
        get() = iWifiService?.asBinder()?.pingBinder() == true
    private var serviceInterface: ServiceInterface? = null

    private suspend fun connect(): Boolean {
        if (isAlive) return true
        unbind()
        val serviceInterface = RootServiceInterface()
        if (!serviceInterface.checkPermission(appContext)) return false
        return suspendCancellableCoroutine { continuation ->
            connection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    Log.d("WifiServiceManager", "onServiceConnected")
                    iWifiService = IWifiService.Stub.asInterface(service)
                    this@WifiServiceManager.serviceInterface = serviceInterface
                    if (continuation.isActive) continuation.resume(true)
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    Log.d("WifiServiceManager", "onServiceDisconnected")
                    iWifiService = null
                    this@WifiServiceManager.serviceInterface = null
                    if (continuation.isActive) continuation.resume(false)
                }

                override fun onBindingDied(name: ComponentName) {
                    Log.d("WifiServiceManager", "onBindingDied")
                    iWifiService = null
                    this@WifiServiceManager.serviceInterface = null
                    if (continuation.isActive) continuation.resume(false)
                }

                override fun onNullBinding(name: ComponentName) {
                    Log.d("WifiServiceManager", "onNullBinding")
                    iWifiService = null
                    this@WifiServiceManager.serviceInterface = null
                    if (continuation.isActive) continuation.resume(false)
                }
            }

            Handler(Looper.getMainLooper()).post {
                serviceInterface.bind(appContext, connection!!)
            }
        }
    }

    fun unbind() {
        connection?.let {
            serviceInterface?.unbind(appContext, it)
        }
        serviceInterface = null
    }

    suspend fun ensureService(): Boolean {
        if (isAlive) return true
        return connect()
    }

    fun getService(): IWifiService? {
        return iWifiService?.takeIf { isAlive }
    }

    fun requireService(): IWifiService {
        return getService() ?: throw IllegalStateException("WifiService is not connected")
    }
}