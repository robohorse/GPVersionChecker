package com.robohorse.gpversionchecker.debug;

import android.text.TextUtils;
import android.util.Log;

import com.robohorse.gpversionchecker.GPVersionChecker;

/**
 * Created by vadim on 14.03.16.
 */
public class ALog {

    private static final String TAG = "GPVersionChecker";

    public static void d(String message) {
        if (GPVersionChecker.useLog && !TextUtils.isEmpty(message)) {
            Log.d(TAG, message);
        }
    }
}
