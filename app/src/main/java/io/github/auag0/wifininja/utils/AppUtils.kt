package io.github.auag0.wifininja.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper

object AppUtils {
    @SuppressLint("PrivateApi")
    fun getAppContext(): Context {
        var c = Class.forName("android.app.ActivityThread")
            .getMethod("currentApplication")
            .invoke(null) as Context
        while (c is ContextWrapper) {
            c = c.baseContext
        }
        return c
    }

    fun isPrivilegedApp(): Boolean {
        val appContext = getAppContext()
        val appInfo = appContext.applicationInfo
        val privateFlags = appInfo::class.java.getDeclaredField("privateFlags").apply {
            isAccessible = true
        }.get(appInfo) as Int
        return (privateFlags and (1 shl 3)) != 0
    }
}