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
}