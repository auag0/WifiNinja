package io.github.auag0.wifininja.ui.main.components.dialogs

import android.net.wifi.WifiConfiguration
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.auag0.wifininja.R
import io.github.auag0.wifininja.models.WifiItem
import io.github.auag0.wifininja.ui.main.components.CheckableListItem
import io.github.auag0.wifininja.utils.WifiConfigurationUtils.ssid

@Composable
fun RestoreDialog(
    restoredConfigs: List<WifiConfiguration>,
    isLoading: Boolean,
    existingWifiList: List<WifiItem>,
    onConfirm: (List<String>) -> Unit,
    onDismissRequest: () -> Unit
) {
    val selectedSsids = remember {
        mutableStateListOf<String>()
    }

    if (isLoading) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text("Loading networks...")
            },
            text = {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
            },
            confirmButton = {}
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = "Restore"
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.restore_networks)
                )
            },
            text = {
                if (restoredConfigs.isEmpty()) {
                    Text(
                        stringResource(id = R.string.no_networks_in_backup)
                    )
                } else {
                    LazyColumn {
                        itemsIndexed(
                            restoredConfigs,
                            key = { _, config -> config.ssid }) { index, config ->
                            val ssid = config.ssid
                            val isChecked = selectedSsids.contains(config.ssid)
                            CheckableListItem(
                                text = ssid,
                                isChecked = isChecked,
                                onCheckedChange = { checked ->
                                    if (isChecked) {
                                        selectedSsids.remove(config.ssid)
                                    } else {
                                        selectedSsids.add(config.ssid)
                                    }
                                },
                                supportingText = if (existingWifiList.any { it.ssid == ssid }) {
                                    stringResource(R.string.network_already_exists)
                                } else {
                                    null
                                },
                                supportingTextColor = if (existingWifiList.any { it.ssid == ssid }) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    Color.Unspecified
                                }
                            )

                            if (index < restoredConfigs.lastIndex) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (restoredConfigs.isEmpty()) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text(
                            text = stringResource(android.R.string.ok)
                        )
                    }
                } else if (selectedSsids.isEmpty()) {
                    TextButton(
                        onClick = {
                            selectedSsids.clear()
                            selectedSsids.addAll(
                                restoredConfigs.map { it.ssid }
                            )
                        }
                    ) {
                        Text(
                            text = stringResource(android.R.string.selectAll)
                        )
                    }
                } else {
                    TextButton(
                        onClick = {
                            onConfirm(selectedSsids)
                        }
                    ) {
                        Text(
                            text = stringResource(android.R.string.ok)
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(
                        text = stringResource(android.R.string.cancel)
                    )
                }
            }
        )
    }
}