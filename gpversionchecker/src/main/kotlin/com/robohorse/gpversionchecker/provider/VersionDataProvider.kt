package com.robohorse.gpversionchecker.provider

import com.robohorse.gpversionchecker.GPVersionChecker
import com.robohorse.gpversionchecker.debug.ALog
import com.robohorse.gpversionchecker.domain.ProjectModel
import com.robohorse.gpversionchecker.domain.Version
import com.robohorse.gpversionchecker.utils.DataParser
import com.robohorse.gpversionchecker.utils.TextFormatter
import org.jsoup.Jsoup
import java.util.*

class VersionDataProvider(
        private val dataParser: DataParser = DataParser(TextFormatter())
) {

    fun obtainDataFromGooglePlay(projectModel: ProjectModel): Version? {
        try {
            val checkVersion = obtainDataFromGooglePlayWithException(
                    projectModel, true, projectModel.currentVersion
            )
            val localizedVersion = obtainDataFromGooglePlayWithException(
                    projectModel, false, projectModel.currentVersion
            )
            val newVersionValue = checkVersion.newVersionCode?.let {
                dataParser.replaceNonDigits(it).toInt()
            } ?: 0
            val currentVersionValue = dataParser.replaceNonDigits(
                    projectModel.currentVersion
            ).toInt()
            val needToUpdate = newVersionValue > currentVersionValue
            return localizedVersion.copy(
                    isNeedToUpdate = needToUpdate,
                    newVersionCode = checkVersion.newVersionCode
            )
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            GPVersionChecker.onResponseReceived(null, throwable)
        }
        return null
    }

    private fun obtainDataFromGooglePlayWithException(
            projectModel: ProjectModel,
            englishLanguage: Boolean,
            currentVersion: String
    ): Version {
        with(projectModel) {
            val language = if (englishLanguage) {
                Locale.ENGLISH.language
            } else {
                Locale.getDefault().language
            }
            val url = "$storeUrl$packageName&hl=$language"
            ALog.d("request params: package - $packageName, current app version: $currentVersion")
            val document = Jsoup.connect(url)
                    .timeout(CONNECTION_TIMEOUT)
                    .userAgent(USER_AGENT)
                    .referrer(REFERRER)
                    .get()
            return dataParser.parse(document, url)
        }
    }
}

private const val REFERRER = "http://www.google.com"
private const val USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0"
private const val CONNECTION_TIMEOUT = 30000
