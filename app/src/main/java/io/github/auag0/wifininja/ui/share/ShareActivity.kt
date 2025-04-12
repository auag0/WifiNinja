package io.github.auag0.wifininja.ui.share

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.auag0.wifininja.theme.AppTheme

class ShareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ShareScreen()
            }
        }
    }

    companion object {
        const val KEY_WIFI_ITEM = "wifi_item"
    }
}