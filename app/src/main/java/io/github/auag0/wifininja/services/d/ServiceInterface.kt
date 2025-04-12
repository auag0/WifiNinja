package io.github.auag0.wifininja.services.d

import android.content.Context
import android.content.ServiceConnection

interface ServiceInterface {
    suspend fun checkPermission(context: Context): Boolean
    fun bind(context: Context, connection: ServiceConnection)
    fun unbind(context: Context, connection: ServiceConnection)
}