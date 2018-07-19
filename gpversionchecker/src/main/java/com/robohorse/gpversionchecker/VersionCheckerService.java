package com.robohorse.gpversionchecker;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.robohorse.gpversionchecker.debug.ALog;
import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.utils.DataParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by robohorse on 06.03.16.
 */
public class VersionCheckerService extends IntentService {
    private static final String REFERRER = "http://www.google.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";

    private static final int CONNECTION_TIMEOUT = 30000;

    public VersionCheckerService() {
        super("GPVersionChecker");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Version version = obtainDataFromGooglePlay();
        if (null != version) {
            ALog.d("Response received: " + version.toString());
            GPVersionChecker.onResponseReceived(version, null);
        }
        stopSelf();
    }

    private Version obtainDataFromGooglePlay() {
        try {
            final String currentVersion = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0)
                    .versionName;
            final Version checkVersion = obtainDataFromGooglePlayWithException(true, currentVersion);
            final Version localizedVersion = obtainDataFromGooglePlayWithException(false, currentVersion);

            final int newVersionValue = Integer.parseInt(DataParser.replaceNonDigits(checkVersion.getNewVersionCode()));
            final int currentVersionValue = Integer.parseInt(DataParser.replaceNonDigits(currentVersion));
            final boolean needToUpdate = newVersionValue > currentVersionValue;
            return new Version
                    .Builder(localizedVersion)
                    .setNeedToUpdate(needToUpdate)
                    .setNewVersionCode(checkVersion.getNewVersionCode())
                    .build();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            GPVersionChecker.onResponseReceived(null, throwable);
        }
        return null;
    }

    private Version obtainDataFromGooglePlayWithException(boolean englishLanguage, String currentVersion)
            throws IOException, NumberFormatException {

        final Context context = getApplicationContext();
        final String packageName = context.getPackageName();
        final String language = englishLanguage ? Locale.ENGLISH.getLanguage() : Locale.getDefault().getLanguage();

        final String url = context.getString(R.string.gpvch_google_play_url) + packageName + "&hl=" + language;
        ALog.d("request params: package - " + packageName + ", current app version: " + currentVersion);

        final Document document = Jsoup.connect(url)
                .timeout(CONNECTION_TIMEOUT)
                .userAgent(USER_AGENT)
                .referrer(REFERRER)
                .get();
        return new DataParser().parse(document, url);
    }
}