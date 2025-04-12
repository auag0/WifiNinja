package io.github.auag0.wifininja.ui.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.auag0.wifininja.models.WifiItem

@Composable
fun MainWifiList(
    innerPadding: PaddingValues,
    wifiList: List<WifiItem>,
    isRefreshing: Boolean,
    onShareClick: (wifiItem: WifiItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.consumeWindowInsets(innerPadding),
        contentPadding = innerPadding
    ) {
        item {
            AnimatedVisibility(
                visible = isRefreshing
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }
        }

        items(wifiList, key = { it.networkId }) { wifiItem ->
            WifiListItem(
                wifiItem = wifiItem,
                onShare = onShareClick
            )
        }
    }
}