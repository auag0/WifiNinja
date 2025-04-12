package io.github.auag0.wifininja.utils

object StringUtils {
    fun String.unquote(): String {
        return this.removeSurrounding("\"")
    }

    fun escapeSpecialCharacters(input: String): String {
        return buildString {
            for (char in input) {
                if (char in "\\,;:") {
                    append('\\')
                }
                append(char)
            }
        }
    }

    fun String.addQuotationIfNeeded(): String =
        if (isEmpty()) ""
        else if (length >= 2 && startsWith("\"") && endsWith("\"")) this
        else "\"$this\""
}