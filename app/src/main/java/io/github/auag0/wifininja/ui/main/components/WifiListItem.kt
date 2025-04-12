package io.github.auag0.wifininja.ui.main.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import io.github.auag0.wifininja.R
import io.github.auag0.wifininja.models.WifiItem

@Composable
fun WifiListItem(
    wifiItem: WifiItem,
    onShare: (wifiItem: WifiItem) -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = wifiItem.ssid
            )
        },
        supportingContent = {
            val hasPassword = wifiItem.password.isNotEmpty()
            Text(
                text = if (hasPassword) {
                    wifiItem.password
                } else {
                    stringResource(R.string.no_password)
                },
                color = if (hasPassword) {
                    Color.Unspecified
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "Wifi"
            )
        },
        trailingContent = {
            Icon(
                modifier = Modifier.clickable {
                    onShare(wifiItem)
                },
                imageVector = Icons.Default.Share,
                contentDescription = "Share"
            )
        }
    )
}