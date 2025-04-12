package io.github.auag0.wifininja.ui.main

sealed class MainDialogs {
    object BackupOrRestore : MainDialogs()
    object Backup : MainDialogs()
    object Restore : MainDialogs()
}