package io.github.auag0.wifininja.ui.main.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.auag0.wifininja.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    onBackupOrRestoreClick: () -> Unit
) {
    LargeTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.saved_wifi)
            )
        },
        actions = {
            IconButton(
                onClick = onBackupOrRestoreClick
            ) {
                Icon(
                    imageVector = Icons.Default.ImportExport,
                    contentDescription = "BackupOrRestore"
                )
            }
        }
    )
}