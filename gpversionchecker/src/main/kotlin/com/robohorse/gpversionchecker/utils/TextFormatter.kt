package com.robohorse.gpversionchecker.utils

import android.text.Html

class TextFormatter {
    fun format(value: String?) = Html.fromHtml(value).toString()
}
