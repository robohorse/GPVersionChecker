package com.robohorse.gpversionchecker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.text.TextUtils;

import com.robohorse.gpversionchecker.domain.Version;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by robohorse on 06.03.16.
 */
public class VersionCheckerService extends Service {
    private static final String REFERRER = "http://www.google.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private static final String DIV_VERSION = "div[itemprop=softwareVersion]";
    private static final String DIV_CHANGES = "div[class=recent-change]";

    private static final int THREAD_SIZE = 1;
    private static final int CONNECTION_TIMEOUT = 30000;

    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        DataGetRunner dataGetRunner = new DataGetRunner();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_SIZE);
        executorService.execute(dataGetRunner);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private class DataGetRunner implements Runnable {
        public void run() {
            Version version = obtainDataFromGooglePlay();

            if (null != version) {
                GPVersionChecker.onResponseReceived(version);
            }
            stopSelf();
        }
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

        if (TextUtils.isEmpty(newVersion) || TextUtils.isEmpty(curVersion) || newVersion.equals(curVersion)) {
            return null;
        }

        final int versionCurrent = Integer.parseInt(curVersion.replace(".", ""));
        final int versionNew = Integer.parseInt(newVersion.replace(".", ""));
        final boolean needToUpdate = versionNew > versionCurrent;

        return new Version(newVersion, changes, needToUpdate);
    }
}