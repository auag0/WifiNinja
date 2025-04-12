package io.github.auag0.wifininja.ui.main.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.auag0.wifininja.R

@Composable
fun BackupOrRestoreDialog(
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = Icons.Default.ImportExport,
                contentDescription = "BackupOrRestore"
            )
        },
        title = {
            Text(
                text = stringResource(R.string.backup_restore)
            )
        },
        text = {
            Column {
                ListItem(
                    modifier = Modifier.clickable {
                        onBackup()
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = AlertDialogDefaults.containerColor
                    ),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Backup"
                        )
                    },
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.backup)
                        )
                    }
                )

                HorizontalDivider()

                ListItem(
                    modifier = Modifier.clickable {
                        onRestore()
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = AlertDialogDefaults.containerColor
                    ),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = "Restore"
                        )
                    },
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.restore)
                        )
                    }
                )
            }
        },
        confirmButton = {
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