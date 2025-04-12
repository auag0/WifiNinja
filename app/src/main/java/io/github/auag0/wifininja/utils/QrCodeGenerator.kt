package io.github.auag0.wifininja.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import java.nio.charset.StandardCharsets
import java.util.EnumMap

object QrCodeGenerator {
    private const val DEFAULT_MARGIN = -1

    fun encodeQrCode(contents: String, size: Int, invert: Boolean): Bitmap =
        encodeQrCode(contents, size, DEFAULT_MARGIN, invert)

    fun encodeQrCode(
        contents: String,
        size: Int,
        margin: Int = DEFAULT_MARGIN,
        invert: Boolean = false
    ): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        if (!isIso88591(contents)) {
            hints[EncodeHintType.CHARACTER_SET] = StandardCharsets.UTF_8.name()
        }
        if (margin != DEFAULT_MARGIN) {
            hints[EncodeHintType.MARGIN] = margin
        }
        val qrBits = MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, size, size, hints)
        @ColorInt val setColor = if (invert) Color.WHITE else Color.BLACK
        @ColorInt val unsetColor = if (invert) Color.BLACK else Color.WHITE
        @ColorInt val pixels = IntArray(size * size)
        for (x in 0 until size) {
            for (y in 0 until size) {
                pixels[x * size + y] = if (qrBits[x, y]) setColor else unsetColor
            }
        }
        return createBitmap(size, size, Bitmap.Config.RGB_565).apply {
            setPixels(pixels, 0, size, 0, 0, size, size)
        }
    }

    private fun isIso88591(contents: String): Boolean =
        StandardCharsets.ISO_8859_1.newEncoder().canEncode(contents)
}