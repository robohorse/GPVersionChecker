package com.robohorse.gpversionchecker.utils

import android.text.TextUtils
import com.robohorse.gpversionchecker.domain.Version
import org.jsoup.nodes.Document

class DataParser(
        private val textFormatter: TextFormatter
) {

    fun parse(document: Document?, url: String?): Version {
        var newVersion = EMPTY_STRING
        document?.let {
            val elements = document.getElementsContainingOwnText(DIV_VERSION)
            for (element in elements) {
                if (element.siblingElements() != null) {
                    val subElements = element.siblingElements()
                    for (subElement in subElements) {
                        newVersion = subElement.text()
                    }
                }
            }
        }
        var changes: String? = EMPTY_STRING
        var description: String? = EMPTY_STRING
        document?.let {
            description = textFormatter.format(document.select(DIV_DESCRIPTION)
                    .html())
            val elements = document.select(DIV_CHANGES)
            if (null != elements) {
                val changesElements = elements.select(DIV_CHANGES)
                if (!changesElements.isEmpty()) {
                    changes = textFormatter.format(changesElements.last().html())
                    if (TextUtils.equals(changes, description)) {
                        changes = null
                    }
                }
            }
        }

        return Version(
                newVersionCode = newVersion,
                changes = changes,
                description = description,
                url = url
        )
    }

    fun replaceNonDigits(value: String) =
            value.replace("[^\\d.]".toRegex(), EMPTY_STRING)
                    .replace(".", EMPTY_STRING)
}

private const val DIV_VERSION = "Current Version"
private const val DIV_CHANGES = "div[class=DWPxHb]"
private const val DIV_DESCRIPTION = "div[itemprop=description]"
private const val EMPTY_STRING = ""
