package com.robohorse.gpversionchecker.debug

import android.util.Log
import com.robohorse.gpversionchecker.GPVersionChecker

object ALog {

    fun d(message: String?) {
        if (GPVersionChecker.useLog) {
            message?.let { Log.d(TAG, message) }
        }
    }
}

private const val TAG = "GPVersionChecker"
