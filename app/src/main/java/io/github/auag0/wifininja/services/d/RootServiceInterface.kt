package io.github.auag0.wifininja.services.d

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import com.topjohnwu.superuser.ipc.RootService
import io.github.auag0.wifininja.services.RootWifiService
import io.github.auag0.wifininja.utils.RootUtils.checkRoot

class RootServiceInterface : ServiceInterface {
    override suspend fun checkPermission(context: Context): Boolean {
        return checkRoot()
    }

    override fun bind(context: Context, connection: ServiceConnection) {
        val intent = Intent(context, RootWifiService::class.java)
        RootService.bind(intent, connection)
    }

    override fun unbind(context: Context, connection: ServiceConnection) {
        RootService.unbind(connection)
    }
}