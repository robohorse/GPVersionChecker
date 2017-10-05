package com.robohorse.gpversionchecker.manager;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.robohorse.gpversionchecker.VersionCheckerService;
import com.robohorse.gpversionchecker.domain.CheckingStrategy;
import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.domain.VersionCheckedException;
import com.robohorse.gpversionchecker.provider.SharedDataProvider;
import com.robohorse.gpversionchecker.utils.DateFormatUtils;

import java.util.Date;

/**
 * Created by v.shchenev on 05/10/2017.
 */

public class ServiceStartManager {
    private final SharedDataProvider sharedDataProvider;
    private final DateFormatUtils formatUtils;

    public ServiceStartManager(SharedDataProvider sharedDataProvider, DateFormatUtils formatUtils) {
        this.sharedDataProvider = sharedDataProvider;
        this.formatUtils = formatUtils;
    }

    public void checkAndStartService(Activity activity, CheckingStrategy strategy) {
        switch (strategy) {
            case ALWAYS:
            case ONE_PER_VERSION: {
                startService(activity);
                break;
            }
            case ONE_PER_VERSION_PER_DAY:
            case ONE_PER_DAY: {
                final long storedTime = sharedDataProvider.provideLastCheckTime();
                final Date storedDate = new Date(storedTime);
                final Date today = formatUtils.formatTodayDate();
                if (today.after(storedDate)) {
                    startService(activity);
                }
                break;
            }
        }
    }

    public void onResulted(CheckingStrategy strategy, Version version) throws VersionCheckedException {
        sharedDataProvider.saveCurrentDate();
        if (null != version) {
            if (strategy == CheckingStrategy.ONE_PER_VERSION || strategy == CheckingStrategy.ONE_PER_VERSION_PER_DAY) {
                final String lastVersion = sharedDataProvider.provideLastVersionCode();
                if (TextUtils.equals(lastVersion, version.getNewVersionCode())) {
                    throw new VersionCheckedException();
                }
            }
            sharedDataProvider.saveCurrentVersion(version);
        }
    }

    private void startService(Activity activity) {
        activity.startService(new Intent(activity, VersionCheckerService.class));
    }
}
