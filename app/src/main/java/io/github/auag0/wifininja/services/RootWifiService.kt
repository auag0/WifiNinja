package io.github.auag0.wifininja.services

import android.content.Intent
import android.os.IBinder
import com.topjohnwu.superuser.ipc.RootService

class RootWifiService : RootService() {
    override fun onBind(intent: Intent): IBinder {
        return WifiServiceImpl()
    }
}