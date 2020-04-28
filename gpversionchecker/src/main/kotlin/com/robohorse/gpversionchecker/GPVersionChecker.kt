package com.robohorse.gpversionchecker

import android.app.Activity
import android.preference.PreferenceManager
import com.robohorse.gpversionchecker.domain.CheckingStrategy
import com.robohorse.gpversionchecker.domain.Version
import com.robohorse.gpversionchecker.domain.VersionCheckedException
import com.robohorse.gpversionchecker.domain.VersionInfoListener
import com.robohorse.gpversionchecker.executor.SyncExecutor
import com.robohorse.gpversionchecker.manager.ServiceStartManager
import com.robohorse.gpversionchecker.manager.UIManager
import com.robohorse.gpversionchecker.provider.SharedDataProvider
import com.robohorse.gpversionchecker.utils.DateFormatUtils
import java.lang.ref.WeakReference

object GPVersionChecker {
    private var activityWeakReference: WeakReference<Activity>? = null
    private var versionInfoListener: VersionInfoListener? = null
    private var strategy: CheckingStrategy? = null
    private var uiManager: UIManager? = null
    private var serviceStartManager: ServiceStartManager? = null
    var useLog = false

    private fun proceed() {
        val activity = activityWeakReference?.get()
                ?: throw IllegalStateException(
                        "Activity cannot be null for GPVersionChecker context"
                )
        serviceStartManager?.checkAndStartService(activity, strategy)
    }

    fun onResponseReceived(version: Version?, throwable: Throwable?) {
        if (null != activityWeakReference) {
            val activity = activityWeakReference?.get()
            if (null != activity && !activity.isFinishing) {
                try {
                    serviceStartManager?.onResulted(strategy, version)
                } catch (e: VersionCheckedException) {
                    return
                }
                if (null != versionInfoListener) {
                    activity.runOnUiThread(Runnable {
                        if (null != version && null == throwable) {
                            versionInfoListener?.onResulted(version)
                        } else if (null != throwable) {
                            versionInfoListener?.onErrorHandled(throwable)
                        }
                    })
                } else if (null != version && version.isNeedToUpdate) {
                    uiManager?.showInfoView(activity, version)
                }
            }
        }
    }

    private fun resetState(activity: Activity) {
        activityWeakReference = WeakReference(activity)
        versionInfoListener = null
        strategy = CheckingStrategy.ALWAYS
    }

    class Builder {
        constructor(activity: Activity) {
            resetState(activity)
            uiManager = UIManager()
            val formatUtils = DateFormatUtils()
            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val sharedDataProvider = SharedDataProvider(preferences, formatUtils)
            serviceStartManager = ServiceStartManager(
                    sharedDataProvider,
                    formatUtils,
                    SyncExecutor()
            )
        }

        constructor(activity: Activity, uiManager: UIManager?) {
            resetState(activity)
            GPVersionChecker.uiManager = uiManager
        }

        /**
         * Set custom version-response subscriber. This subscriber will disable default dialog window.
         *
         * @return Builder
         */
        fun setVersionInfoListener(versionInfoListener: VersionInfoListener?): Builder {
            GPVersionChecker.versionInfoListener = versionInfoListener
            return this
        }

        /**
         * Set logging
         *
         * @return Builder
         */
        fun setLoggingEnable(useLog: Boolean): Builder {
            GPVersionChecker.useLog = useLog
            return this
        }

        /**
         * Set strategy of version-checking: ALWAYS, ONE_PER_DAY, ONE_PER_VERSION, ONE_PER_VERSION_PER_DAY
         *
         * @return Builder
         */
        fun setCheckingStrategy(strategy: CheckingStrategy?): Builder {
            GPVersionChecker.strategy = strategy
            return this
        }

        /**
         * Create checking request
         *
         * @return Builder
         */
        fun create(): Builder {
            proceed()
            return this
        }
    }
}
