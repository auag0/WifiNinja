package io.github.auag0.wifininja.utils

import com.topjohnwu.superuser.Shell

object RootUtils {
    fun checkRoot(): Boolean {
        val shell = Shell.getShell()
        if (shell.isRoot) {
            return true
        } else {
            shell.close()
            return false
        }
    }
}