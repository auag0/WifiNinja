package io.github.auag0.wifininja.ui.share

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.auag0.wifininja.R
import io.github.auag0.wifininja.models.toast.ToastMessage
import io.github.auag0.wifininja.ui.share.components.ActionButtons
import io.github.auag0.wifininja.ui.share.components.QRCodeBox
import io.github.auag0.wifininja.ui.share.components.WifiInfoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    viewModel: ShareViewModel = viewModel()
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

    val wifiItem by viewModel.wifiItem.collectAsState()
    val qrCodeBitmap by viewModel.qrCodeBitmap.collectAsState()
    val density = LocalDensity.current
    val qrCodeSize = 256.dp

    val qrCodeSizePx = remember {
        with(density) { qrCodeSize.toPx().toInt() }
    }

    LaunchedEffect(qrCodeSizePx) {
        viewModel.loadQrCode(qrCodeSizePx)
    }

    val clipboardManager = LocalClipboardManager.current

    val requestStorageForSaveQRCode = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.handleSaveQRCode()
        } else {
            Toast.makeText(context, R.string.storage_permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.share_wifi)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QRCodeBox(
                qrCodeBitmap = qrCodeBitmap,
                qrCodeSize = qrCodeSize
            )

            WifiInfoCard(
                wifiItem = wifiItem,
                copyText = { text ->
                    clipboardManager.setText(AnnotatedString(text))
                }
            )

            ActionButtons(
                readyQRCode = qrCodeBitmap != null,
                onShareQRCode = viewModel::handleShareQRCode,
                onShareViaText = {
                    val text = "SSID:${wifiItem.ssid}\nPASS:${wifiItem.password}"
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, text)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(intent, wifiItem.ssid)
                    context.startActivity(shareIntent)
                },
                onSaveQRCode = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        viewModel.handleSaveQRCode()
                    } else {
                        requestStorageForSaveQRCode.launch(WRITE_EXTERNAL_STORAGE)
                    }
                }
            )
        }
    }
}