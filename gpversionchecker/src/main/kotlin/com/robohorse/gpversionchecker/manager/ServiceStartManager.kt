package com.robohorse.gpversionchecker.manager

import android.content.Context
import com.robohorse.gpversionchecker.domain.CheckingStrategy
import com.robohorse.gpversionchecker.domain.Version
import com.robohorse.gpversionchecker.domain.VersionCheckedException
import com.robohorse.gpversionchecker.executor.SyncExecutor
import com.robohorse.gpversionchecker.provider.SharedDataProvider
import com.robohorse.gpversionchecker.utils.DateFormatUtils
import java.util.*

class ServiceStartManager(
        private val sharedDataProvider: SharedDataProvider,
        private val formatUtils: DateFormatUtils,
        private val syncExecutor: SyncExecutor
) {
    fun checkAndStartService(context: Context, strategy: CheckingStrategy?) {
        when (strategy) {
            CheckingStrategy.ALWAYS, CheckingStrategy.ONE_PER_VERSION -> {
                syncExecutor.startSync(context)
            }
            CheckingStrategy.ONE_PER_VERSION_PER_DAY, CheckingStrategy.ONE_PER_DAY -> {
                val storedTime = sharedDataProvider.provideLastCheckTime()
                val storedDate = Date(storedTime)
                val today = formatUtils.formatTodayDate()
                if (today?.after(storedDate) == true) {
                    syncExecutor.startSync(context)
                }
            }
        }
    }

    fun onResulted(strategy: CheckingStrategy?, version: Version?) {
        sharedDataProvider.saveCurrentDate()
        version?.let {
            if (strategy == CheckingStrategy.ONE_PER_VERSION ||
                    strategy == CheckingStrategy.ONE_PER_VERSION_PER_DAY) {
                val lastVersion = sharedDataProvider.provideLastVersionCode()
                if (lastVersion == version.newVersionCode) {
                    throw VersionCheckedException()
                }
            }
            sharedDataProvider.saveCurrentVersion(version)
        }
    }
}
