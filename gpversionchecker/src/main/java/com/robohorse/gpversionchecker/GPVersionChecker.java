package com.robohorse.gpversionchecker;

import android.app.Activity;

import com.robohorse.gpversionchecker.base.CheckingStrategy;
import com.robohorse.gpversionchecker.base.VersionInfoListener;
import com.robohorse.gpversionchecker.delegate.UIDelegate;
import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.domain.VersionCheckedException;
import com.robohorse.gpversionchecker.manager.ServiceStartManager;

import java.lang.ref.WeakReference;

/**
 * Created by robohorse on 06.03.16.
 */
public class GPVersionChecker {
    private static WeakReference<Activity> activityWeakReference;
    private static VersionInfoListener versionInfoListener;
    private static CheckingStrategy strategy;
    private static UIDelegate uiDelegate;
    private static ServiceStartManager serviceStartManager;
    public static boolean useLog;

    private static void proceed() {
        final Activity activity = activityWeakReference.get();
        if (null == activity) {
            throw new IllegalStateException("Activity cannot be null for GPVersionChecker context");
        }
        serviceStartManager.checkAndStartService(activity, strategy);
    }

    static void onResponseReceived(final Version version, final Throwable throwable) {
        if (null != activityWeakReference) {
            final Activity activity = activityWeakReference.get();

            if (null != activity && !activity.isFinishing()) {
                try {
                    serviceStartManager.onResulted(activity, strategy, version);
                } catch (VersionCheckedException e) {
                    return;
                }

                if (null != versionInfoListener) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != version && null == throwable) {
                                versionInfoListener.onResulted(version);
                            } else if (null != throwable) {
                                versionInfoListener.onErrorHandled(throwable);
                            }
                        }
                    });
                } else if (null != version && version.isNeedToUpdate()) {
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
            serviceStartManager = new ServiceStartManager();
        }

        protected Builder(Activity activity, UIDelegate uiDelegate) {
            resetState(activity);
            GPVersionChecker.uiDelegate = uiDelegate;
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
         * Set strategy of version-checking: ALWAYS, ONE_PER_DAY, ONE_PER_VERSION
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
