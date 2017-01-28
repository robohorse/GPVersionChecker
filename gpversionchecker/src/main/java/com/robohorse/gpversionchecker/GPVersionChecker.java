package com.robohorse.gpversionchecker;

import android.app.Activity;
import android.content.Intent;

import com.robohorse.gpversionchecker.base.CheckingStrategy;
import com.robohorse.gpversionchecker.base.VersionInfoListener;
import com.robohorse.gpversionchecker.debug.ALog;
import com.robohorse.gpversionchecker.delegate.UIDelegate;
import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.provider.SharedDataProvider;

import java.lang.ref.WeakReference;

/**
 * Created by robohorse on 06.03.16.
 */
public class GPVersionChecker {
    private static WeakReference<Activity> activityWeakReference;
    private static VersionInfoListener versionInfoListener;
    private static CheckingStrategy strategy;
    private static UIDelegate uiDelegate;
    private static SharedDataProvider sharedDataProvider;
    public static boolean useLog;

    private static void proceed() {
        final Activity activity = activityWeakReference.get();
        if (null == activity) {
            throw new IllegalStateException("Activity cannot be null for GPVersionChecker context");
        }

        final boolean checkRequired = sharedDataProvider.needToCheckVersion(activity);
        if (strategy == CheckingStrategy.ALWAYS ||
                (strategy == CheckingStrategy.ONE_PER_DAY && checkRequired)) {
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
                sharedDataProvider.saveCurrentDate(activity);

                if (null != versionInfoListener) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            versionInfoListener.onResulted(version);
                        }
                    });
                } else if (version.isNeedToUpdate()) {
                    uiDelegate.showInfoView(activity, version);
                }
            }
        }
    }

    private static void resetState(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
        versionInfoListener = null;
        strategy = CheckingStrategy.ALWAYS;
    }

    public static class Builder {

        public Builder(Activity activity) {
            resetState(activity);
            uiDelegate = new UIDelegate();
            sharedDataProvider = new SharedDataProvider();
        }

        protected Builder(Activity activity, UIDelegate uiDelegate, SharedDataProvider sharedDataProvider) {
            resetState(activity);
            GPVersionChecker.uiDelegate = uiDelegate;
            GPVersionChecker.sharedDataProvider = sharedDataProvider;
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
