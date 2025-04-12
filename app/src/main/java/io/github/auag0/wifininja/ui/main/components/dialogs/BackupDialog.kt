package io.github.auag0.wifininja.ui.main.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.github.auag0.wifininja.R
import io.github.auag0.wifininja.models.WifiItem
import io.github.auag0.wifininja.ui.main.components.CheckableListItem

@Composable
fun BackupDialog(
    wifiList: List<WifiItem>,
    onConfirm: (selectedNetworkIds: List<Int>) -> Unit,
    onDismissRequest: () -> Unit
) {
    val selectedNetworkIds = remember {
        mutableStateListOf<Int>()
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = Icons.Default.Backup,
                contentDescription = "Backup"
            )
        },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.backup)
                )
                Text(
                    text = stringResource(R.string.wifi_backup_warning),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            LazyColumn {
                itemsIndexed(wifiList, key = { _, item -> item.networkId }) { index, item ->
                    val isChecked = selectedNetworkIds.contains(item.networkId)
                    CheckableListItem(
                        text = item.ssid,
                        isChecked = isChecked,
                        onCheckedChange = { checked ->
                            if (checked) {
                                selectedNetworkIds.add(item.networkId)
                            } else {
                                selectedNetworkIds.remove(item.networkId)
                            }
                        }
                    )
                    if (index < wifiList.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            if (selectedNetworkIds.isEmpty()) {
                TextButton(
                    onClick = {
                        selectedNetworkIds.clear()
                        selectedNetworkIds.addAll(
                            wifiList.map {
                                it.networkId
                            }
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
                        onConfirm(selectedNetworkIds)
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