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
    private final DateFormatUtils formatUtils;

    public SharedDataProvider(SharedPreferences sharedPreferences, DateFormatUtils formatUtils) {
        this.sharedPreferences = sharedPreferences;
        this.formatUtils = formatUtils;
    }

    public void saveCurrentDate() {
        sharedPreferences
                .edit()
                .putLong(GPVCH_TIME, formatUtils.formatTodayDate().getTime())
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
