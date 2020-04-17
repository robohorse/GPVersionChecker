package com.robohorse.gpversionchecker.provider

import android.content.SharedPreferences
import com.robohorse.gpversionchecker.domain.Version
import com.robohorse.gpversionchecker.utils.DateFormatUtils

class SharedDataProvider(
        private val sharedPreferences: SharedPreferences,
        private val formatUtils: DateFormatUtils
) {

    fun saveCurrentDate() {
        formatUtils.formatTodayDate()?.let {
            sharedPreferences
                    .edit()
                    .putLong(GPVCH_TIME, it.time)
                    .apply()
        }
    }

    fun provideLastCheckTime(): Long {
        return sharedPreferences.getLong(GPVCH_TIME, 0L)
    }

    fun saveCurrentVersion(version: Version) {
        sharedPreferences
                .edit()
                .putString(GPVCH_VERSION, version.newVersionCode)
                .apply()
    }

    fun provideLastVersionCode(): String? {
        return sharedPreferences.getString(GPVCH_VERSION, null)
    }
}

private const val GPVCH_TIME = "gpvch_time"
private const val GPVCH_VERSION = "gpvch_version"
