package io.github.auag0.wifininja.ui.main

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.auag0.wifininja.models.toast.ToastMessage
import io.github.auag0.wifininja.ui.main.components.MainFloatingActionButton
import io.github.auag0.wifininja.ui.main.components.MainTopBar
import io.github.auag0.wifininja.ui.main.components.MainWifiList
import io.github.auag0.wifininja.ui.main.components.dialogs.BackupDialog
import io.github.auag0.wifininja.ui.main.components.dialogs.BackupOrRestoreDialog
import io.github.auag0.wifininja.ui.main.components.dialogs.RestoreDialog
import io.github.auag0.wifininja.ui.share.ShareActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { toastMessage ->
            val msg = with(toastMessage) {
                when (this) {
                    is ToastMessage.Text -> message
                    is ToastMessage.ResId -> context.getString(resId, *formatArgs)
                }
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val wifiList by viewModel.wifiList.collectAsState()
    var currentDialog by remember { mutableStateOf<MainDialogs?>(null) }

    val restoreLauncher = rememberLauncherForActivityResult(
        OpenDocument()
    ) { uri: Uri? ->
        uri?.let { safeUri ->
            viewModel.loadRestoredConfigs(safeUri)
            currentDialog = MainDialogs.Restore
        }
    }

    val backupLauncher = rememberLauncherForActivityResult(
        CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let { safeUri ->
            viewModel.handleBackup(safeUri)
            currentDialog = null
        }
    }

    Scaffold(
        topBar = {
            MainTopBar(
                onBackupOrRestoreClick = {
                    currentDialog = MainDialogs.BackupOrRestore
                }
            )
        },
        floatingActionButton = {
            MainFloatingActionButton(
                isRefreshing = isRefreshing,
                onClick = viewModel::loadWifiList
            )
        }
    ) { innerPadding ->
        when (currentDialog) {
            MainDialogs.BackupOrRestore -> {
                BackupOrRestoreDialog(
                    onBackup = {
                        currentDialog = MainDialogs.Backup
                    },
                    onRestore = {
                        restoreLauncher.launch(arrayOf("application/json"))
                    },
                    onDismissRequest = {
                        currentDialog = null
                    }
                )
            }

            MainDialogs.Backup -> {
                BackupDialog(
                    wifiList = wifiList,
                    onConfirm = { selectedNetworkIds ->
                        viewModel.updateSelectedNetworkIds(selectedNetworkIds)
                        backupLauncher.launch("wifi_key_backup.json")
                    },
                    onDismissRequest = {
                        currentDialog = null
                    }
                )
            }

            MainDialogs.Restore -> {
                val restoredConfigs by viewModel.restoredConfigs.collectAsState()
                val isLoadingRestoredConfigs by viewModel.isLoadingRestoredConfigs.collectAsState()

                RestoreDialog(
                    restoredConfigs = restoredConfigs,
                    isLoading = isLoadingRestoredConfigs,
                    existingWifiList = wifiList,
                    onConfirm = { selectedSsids ->
                        viewModel.handleRestoreSelected(selectedSsids)
                        currentDialog = null
                    },
                    onDismissRequest = {
                        currentDialog = null
                    }
                )
            }

            null -> {}
        }

        MainWifiList(
            innerPadding = innerPadding,
            wifiList = wifiList,
            isRefreshing = isRefreshing,
            onShareClick = { wifiItem ->
                val intent = Intent(context, ShareActivity::class.java)
                intent.putExtra(ShareActivity.KEY_WIFI_ITEM, wifiItem)
                context.startActivity(intent)
            }
        )
    }
}