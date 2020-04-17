package com.robohorse.gpversionchecker.executor

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.robohorse.gpversionchecker.GPVersionChecker
import com.robohorse.gpversionchecker.R
import com.robohorse.gpversionchecker.debug.ALog
import com.robohorse.gpversionchecker.domain.ProjectModel
import com.robohorse.gpversionchecker.domain.Version
import com.robohorse.gpversionchecker.provider.VersionDataProvider

class SyncExecutor {
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.arg1 == MESSAGE_CODE) {
                GPVersionChecker.onResponseReceived(msg.obj as? Version?, null)
            }
        }
    }

    fun startSync(context: Context) {
        val projectModel = ProjectModel(
                packageName = context.packageName,
                storeUrl = context.getString(R.string.gpvch_google_play_url),
                currentVersion = context.applicationContext
                        .packageManager
                        .getPackageInfo(
                                context.applicationContext.packageName, DEFAULT_FLAG
                        ).versionName
        )
        Thread(Runnable {
            VersionDataProvider().obtainDataFromGooglePlay(projectModel)?.let {
                ALog.d("Response received: $it")
                handler.sendMessage(Message().apply {
                    arg1 = MESSAGE_CODE
                    obj = it
                })
            }
        }).start()
    }
}

private const val MESSAGE_CODE = 233
private const val DEFAULT_FLAG = 0
