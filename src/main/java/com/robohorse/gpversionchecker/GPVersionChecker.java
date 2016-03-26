package com.robohorse.gpversionchecker;

import android.app.Activity;
import android.content.Intent;

import com.robohorse.gpversionchecker.base.CheckingStrategy;
import com.robohorse.gpversionchecker.debug.ALog;
import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.helper.SharedPrefsHelper;
import com.robohorse.gpversionchecker.helper.UIHelper;
import com.robohorse.gpversionchecker.base.VersionInfoListener;

import java.lang.ref.WeakReference;

/**
 * Created by robohorse on 06.03.16.
 */
public class GPVersionChecker {
    private static WeakReference<Activity> activityWeakReference;
    private static VersionInfoListener versionInfoListener;
    private static CheckingStrategy strategy = CheckingStrategy.ALWAYS;
    public static boolean useLog;

    private static void proceed() {
        Activity activity = activityWeakReference.get();
        if (null == activity) {
            throw new RuntimeException("Activity cannot be null for GPVersionChecker context");
        }

        if (strategy == CheckingStrategy.ALWAYS ||
                (strategy == CheckingStrategy.ONE_PER_DAY && SharedPrefsHelper.needToCheckVersion(activity))) {
            activity.startService(new Intent(activity, VersionCheckerService.class));
        } else {
            ALog.d("Skipped");
        }
    }

    protected static void onResponseReceived(final Version version) {
        if (null != activityWeakReference) {
            Activity activity = activityWeakReference.get();

            if (null == activity || activity.isFinishing()) {
                return;
            }

            SharedPrefsHelper.saveCurrentDate(activity);

            if (null != versionInfoListener) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        versionInfoListener.onResulted(version);
                    }
                });
            } else if (version.isNeedToUpdate()) {
                UIHelper.showInfoView(activity, version);
            }
        }
    }

    public static class Builder {

        public Builder(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
            GPVersionChecker.versionInfoListener = null;
            GPVersionChecker.strategy = CheckingStrategy.ALWAYS;
        }

        public Builder setVersionInfoListener(VersionInfoListener versionInfoListener) {
            GPVersionChecker.versionInfoListener = versionInfoListener;
            return this;
        }

        public Builder setLoggingEbable(boolean useLog) {
            GPVersionChecker.useLog = useLog;
            return this;
        }

        public Builder setCheckingStrategy(CheckingStrategy strategy) {
            GPVersionChecker.strategy = strategy;
            return this;
        }

        public Builder create() {
            proceed();
            return this;
        }
    }
}
