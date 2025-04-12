package io.github.auag0.wifininja.ui.share.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.auag0.wifininja.R
import io.github.auag0.wifininja.models.WifiItem

@Composable
fun WifiInfoCard(
    wifiItem: WifiItem,
    copyText: (text: String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.wifi_info),
                style = MaterialTheme.typography.titleMedium
            )

            val hasPassword = wifiItem.password.isNotEmpty()
            WifiInfoRow(
                label = stringResource(R.string.label_ssid),
                value = wifiItem.ssid,
                onCopy = {
                    copyText(wifiItem.ssid)
                }
            )

            WifiInfoRow(
                label = stringResource(R.string.label_password),
                value = if (hasPassword) {
                    wifiItem.password
                } else {
                    stringResource(R.string.no_password)
                },
                valueColor = if (hasPassword) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.primary
                },
                onCopy = if (hasPassword) {
                    {
                        copyText(wifiItem.password)
                    }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
fun WifiInfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    onCopy: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.3f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SelectionContainer(
            modifier = Modifier.weight(0.7f)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor
            )
        }

        if (onCopy != null) {
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        }
    }
}