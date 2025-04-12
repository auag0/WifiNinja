package io.github.auag0.wifininja.ui.share

import android.app.Application
import android.content.ClipData
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.wifi.WifiConfiguration.KeyMgmt
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.github.auag0.wifininja.R
import io.github.auag0.wifininja.models.WifiItem
import io.github.auag0.wifininja.models.toast.ToastMessage
import io.github.auag0.wifininja.utils.QrCodeGenerator
import io.github.auag0.wifininja.utils.StringUtils.escapeSpecialCharacters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.BitSet
import java.util.Date
import java.util.Locale

class ShareViewModel(
    private val app: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(app) {
    private val _toastMessage = MutableSharedFlow<ToastMessage>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val _wifiItem = MutableStateFlow(
        requireNotNull(
            savedStateHandle.get<WifiItem>(ShareActivity.KEY_WIFI_ITEM)
        ) { "WifiItem must be provided" }
    )
    val wifiItem = _wifiItem.asStateFlow()

    private val _qrCodeBitmap = MutableStateFlow<Bitmap?>(null)
    val qrCodeBitmap = _qrCodeBitmap.asStateFlow()

    private suspend fun sendToast(message: ToastMessage) {
        _toastMessage.emit(message)
    }

    fun loadQrCode(sizeInPx: Int) {
        val wifiItem = wifiItem.value
        viewModelScope.launch {
            val qrCode = generateQrCode(wifiItem)
            _qrCodeBitmap.value = withContext(Dispatchers.IO) {
                QrCodeGenerator.encodeQrCode(qrCode, sizeInPx)
            }
        }
    }

    private fun generateQrCode(wifiItem: WifiItem): String {
        return with(wifiItem) {
            val security = getSecurityString(wifiItem.allowedKeyManagement, password)
            buildString {
                append("WIFI:")
                append("S:${escapeSpecialCharacters(ssid)};")
                append("T:${security.ifEmpty { "" }};")
                append("P:${escapeSpecialCharacters(password).ifEmpty { "" }};")
                append("H:$hiddenSSID;;")
            }
        }
    }

    private fun getSecurityString(allowedKeyManagement: BitSet, password: String): String {
        return when {
            allowedKeyManagement.get(KeyMgmt.SAE) -> "SAE"
            allowedKeyManagement.get(KeyMgmt.OWE) -> "nopass"
            allowedKeyManagement.get(KeyMgmt.WPA_PSK) || allowedKeyManagement.get(KeyMgmt.WPA2_PSK) -> "WPA"
            password.isEmpty() -> "nopass"
            else -> "WEP"
        }
    }

    fun handleSaveQRCode() {
        viewModelScope.launch(Dispatchers.Default) {
            val bitmap = qrCodeBitmap.value ?: return@launch
            val wifiItem = wifiItem.value

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.DISPLAY_NAME, "${wifiItem.ssid}_${timeStamp}.png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val volumeUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val cr = app.contentResolver
            var imageUri: Uri? = null
            try {
                imageUri = cr.insert(volumeUri, values)
                requireNotNull(imageUri) { "Failed to create image" }

                withContext(Dispatchers.IO) {
                    cr.openOutputStream(imageUri)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    } ?: throw IllegalStateException("Failed to open output stream")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                imageUri?.let { uri -> cr.delete(uri, null, null) }
                sendToast(ToastMessage.ResId(R.string.failed_save_qr_code))
                return@launch
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                cr.update(imageUri, values, null, null)
            }

            sendToast(ToastMessage.ResId(R.string.saved_qr_code))
        }
    }

    fun handleShareQRCode() {
        viewModelScope.launch(Dispatchers.Default) {
            val bitmap = qrCodeBitmap.value ?: return@launch
            val wifiItem = wifiItem.value

            val file = app.getFileStreamPath("qr_code.png")
            withContext(Dispatchers.IO) {
                file.outputStream().use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
            file.deleteOnExit()

            val uri = FileProvider.getUriForFile(app, "${app.packageName}.provider", file)
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                clipData = ClipData.newRawUri(wifiItem.ssid, uri)
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/png"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            val shareIntent = Intent.createChooser(intent, wifiItem.ssid)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            app.startActivity(shareIntent)
        }
    }
}