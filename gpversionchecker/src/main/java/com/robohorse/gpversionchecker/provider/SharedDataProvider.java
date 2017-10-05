package com.robohorse.gpversionchecker.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.utils.DateFormatUtils;

/**
 * Created by robohorse on 06.03.16.
 */
public class SharedDataProvider {
    private static final String GPVCH_TIME = "gpvch_time";
    private static final String GPVCH_VERSION = "gpvch_version";

    public static void saveCurrentDate(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences
                .edit()
                .putLong(GPVCH_TIME, DateFormatUtils.formatTodayDate().getTime())
                .apply();
    }

    public static long provideLastCheckTime(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(GPVCH_TIME, 0L);
    }

    public static void saveCurrentVersion(Context context, Version version) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences
                .edit()
                .putString(GPVCH_VERSION, version.getNewVersionCode())
                .apply();
    }

    public static String provideLastVersionCode(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(GPVCH_VERSION, null);
    }
}
