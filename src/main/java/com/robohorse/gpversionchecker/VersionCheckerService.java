package com.robohorse.gpversionchecker;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.text.TextUtils;

import com.robohorse.gpversionchecker.debug.ALog;
import com.robohorse.gpversionchecker.domain.Version;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by robohorse on 06.03.16.
 */
public class VersionCheckerService extends IntentService {
    private static final String REFERRER = "http://www.google.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private static final String DIV_VERSION = "div[itemprop=softwareVersion]";
    private static final String DIV_CHANGES = "div[class=recent-change]";

    private static final int CONNECTION_TIMEOUT = 30000;

    public VersionCheckerService() {
        super("GPVersionChecker");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Version version = obtainDataFromGooglePlay();
        if (null != version) {
            ALog.d("Response received: " + version.toString());
            GPVersionChecker.onResponseReceived(version);
        }
        stopSelf();
    }

    private Version obtainDataFromGooglePlay() {
        try {
            return obtainDataFromGooglePlayWithException();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Version obtainDataFromGooglePlayWithException()
            throws IOException, NumberFormatException, PackageManager.NameNotFoundException {

        Context context = getApplicationContext();
        final String packageName = context.getPackageName();
        final String curVersion = context.getPackageManager().getPackageInfo(packageName, 0).versionName;

        ALog.d("request params: package - " + packageName + ", current app version: " + curVersion);

        final Document document = Jsoup.connect(context.getString(R.string.gpvch_google_play_url) + packageName)
                .timeout(CONNECTION_TIMEOUT)
                .userAgent(USER_AGENT)
                .referrer(REFERRER)
                .get();

        final String newVersion = document.select(DIV_VERSION)
                .first()
                .ownText();

        final String changes = document.select(DIV_CHANGES)
                .html();

        ALog.d("current version: " + curVersion + "; google play version: " + newVersion);

        if (TextUtils.isEmpty(newVersion) || TextUtils.isEmpty(curVersion) || newVersion.equals(curVersion)) {
            return null;
        }

        final boolean needToUpdate = !curVersion.equals(newVersion);

        return new Version(newVersion, changes, needToUpdate);
    }
}