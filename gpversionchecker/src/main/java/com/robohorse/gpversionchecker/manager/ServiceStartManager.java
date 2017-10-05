package com.robohorse.gpversionchecker.manager;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.robohorse.gpversionchecker.VersionCheckerService;
import com.robohorse.gpversionchecker.base.CheckingStrategy;
import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.domain.VersionCheckedException;
import com.robohorse.gpversionchecker.provider.SharedDataProvider;
import com.robohorse.gpversionchecker.utils.DateFormatUtils;

import java.util.Date;

/**
 * Created by v.shchenev on 05/10/2017.
 */

public class ServiceStartManager {

    public void checkAndStartService(Activity activity, CheckingStrategy strategy) {
        switch (strategy) {
            case ALWAYS:
            case ONE_PER_VERSION: {
                startService(activity);
                break;
            }
            case ONE_PER_VERSION_PER_DAY:
            case ONE_PER_DAY: {
                final long storedTime = SharedDataProvider.provideLastCheckTime(activity);
                final Date storedDate = new Date(storedTime);
                final Date today = DateFormatUtils.formatTodayDate();
                if (today.after(storedDate)) {
                    startService(activity);
                }
                break;
            }
        }
    }

    public void onResulted(Activity activity, CheckingStrategy strategy, Version version) throws VersionCheckedException {
        SharedDataProvider.saveCurrentDate(activity);
        if (null != version) {
            if (strategy == CheckingStrategy.ONE_PER_VERSION || strategy == CheckingStrategy.ONE_PER_VERSION_PER_DAY) {
                final String lastVersion = SharedDataProvider.provideLastVersionCode(activity);
                if (TextUtils.equals(lastVersion, version.getNewVersionCode())) {
                    throw new VersionCheckedException();
                }
            }
            SharedDataProvider.saveCurrentVersion(activity, version);
        }
    }

    private void startService(Activity activity) {
        activity.startService(new Intent(activity, VersionCheckerService.class));
    }
}
