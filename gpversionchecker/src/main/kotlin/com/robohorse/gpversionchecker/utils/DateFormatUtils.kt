package com.robohorse.gpversionchecker.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateFormatUtils {
    private val formatter = SimpleDateFormat(DATE_FORMAT, Locale.UK)

    fun formatTodayDate(): Date? {
        val today = Date(System.currentTimeMillis())
        try {
            return formatter.parse(formatter.format(today))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return today
    }
}

private const val DATE_FORMAT = "dd/MM/yyyy"
