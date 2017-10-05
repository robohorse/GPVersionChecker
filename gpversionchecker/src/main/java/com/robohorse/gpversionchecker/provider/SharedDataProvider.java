package com.robohorse.gpversionchecker.provider;

import android.content.SharedPreferences;

import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.utils.DateFormatUtils;

/**
 * Created by robohorse on 06.03.16.
 */
public class SharedDataProvider {
    private static final String GPVCH_TIME = "gpvch_time";
    private static final String GPVCH_VERSION = "gpvch_version";

    private final SharedPreferences sharedPreferences;

    public SharedDataProvider(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void saveCurrentDate() {
        sharedPreferences
                .edit()
                .putLong(GPVCH_TIME, DateFormatUtils.formatTodayDate().getTime())
                .apply();
    }

    public long provideLastCheckTime() {
        return sharedPreferences.getLong(GPVCH_TIME, 0L);
    }

    public void saveCurrentVersion(Version version) {
        sharedPreferences
                .edit()
                .putString(GPVCH_VERSION, version.getNewVersionCode())
                .apply();
    }

    public String provideLastVersionCode() {
        return sharedPreferences.getString(GPVCH_VERSION, null);
    }
}
