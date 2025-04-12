package io.github.auag0.wifininja.models.toast

import androidx.annotation.StringRes

sealed class ToastMessage {
    data class Text(val message: String) : ToastMessage()
    data class ResId(
        @StringRes val resId: Int,
        val formatArgs: Array<out Any> = emptyArray()
    ) : ToastMessage() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ResId

            if (resId != other.resId) return false
            if (!formatArgs.contentEquals(other.formatArgs)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = resId
            result = 31 * result + formatArgs.contentHashCode()
            return result
        }
    }
}