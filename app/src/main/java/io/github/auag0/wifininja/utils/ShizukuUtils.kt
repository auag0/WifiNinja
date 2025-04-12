package io.github.auag0.wifininja.utils

import android.os.Build

object ShizukuUtils {
    fun supportsShizuku(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }
}