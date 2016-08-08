package com.robohorse.gpversionchecker;

import android.app.Activity;
import android.content.Intent;

import com.robohorse.gpversionchecker.base.CheckingStrategy;
import com.robohorse.gpversionchecker.base.VersionInfoListener;
import com.robohorse.gpversionchecker.debug.ALog;
import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.helper.SharedPrefsHelper;
import com.robohorse.gpversionchecker.helper.UIHelper;

import java.lang.ref.WeakReference;

/**
 * Created by robohorse on 06.03.16.
 */
public class GPVersionChecker {
    private static WeakReference<Activity> activityWeakReference;
    private static VersionInfoListener versionInfoListener;
    private static CheckingStrategy strategy;
    public static boolean useLog;
    private static UIHelper uiHelper;
    private static SharedPrefsHelper sharedPrefsHelper;

    private static void proceed() {
        Activity activity = activityWeakReference.get();
        if (null == activity) {
            throw new RuntimeException("Activity cannot be null for GPVersionChecker context");
        }

        if (strategy == CheckingStrategy.ALWAYS ||
                (strategy == CheckingStrategy.ONE_PER_DAY && sharedPrefsHelper.needToCheckVersion(activity))) {
            startService(activity);
        } else {
            ALog.d("Skipped");
        }
    }

    private static void startService(Activity activity) {
        activity.startService(new Intent(activity, VersionCheckerService.class));
    }

    protected static void onResponseReceived(final Version version) {
        if (null != activityWeakReference) {
            Activity activity = activityWeakReference.get();

            if (null != activity && !activity.isFinishing()) {
                sharedPrefsHelper.saveCurrentDate(activity);

                if (null != versionInfoListener) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            versionInfoListener.onResulted(version);
                        }
                    });
                } else if (version.isNeedToUpdate()) {
                    uiHelper.showInfoView(activity, version);
                }
            }
        }
    }

    private static void resetState(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
        GPVersionChecker.versionInfoListener = null;
        GPVersionChecker.strategy = CheckingStrategy.ALWAYS;
    }

    public static class Builder {

        public Builder(Activity activity) {
            resetState(activity);
            uiHelper = new UIHelper();
            sharedPrefsHelper = new SharedPrefsHelper();
        }

        protected Builder(Activity activity, UIHelper uiHelper, SharedPrefsHelper sharedPrefsHelper) {
            resetState(activity);
            GPVersionChecker.uiHelper = uiHelper;
            GPVersionChecker.sharedPrefsHelper = sharedPrefsHelper;
        }

        /**
         * Set custom version-response subscriber. This subscriber will disable default dialog window.
         *
         * @return Builder
         */
        public Builder setVersionInfoListener(VersionInfoListener versionInfoListener) {
            GPVersionChecker.versionInfoListener = versionInfoListener;
            return this;
        }

        /**
         * Set logging
         *
         * @return Builder
         */
        public Builder setLoggingEnable(boolean useLog) {
            GPVersionChecker.useLog = useLog;
            return this;
        }

        /**
         * Set strategy of version-checking: ALWAYS, ONE_PER_DAY
         *
         * @return Builder
         */
        public Builder setCheckingStrategy(CheckingStrategy strategy) {
            GPVersionChecker.strategy = strategy;
            return this;
        }

        /**
         * Create checking request
         *
         * @return Builder
         */
        public Builder create() {
            proceed();
            return this;
        }
    }
}
