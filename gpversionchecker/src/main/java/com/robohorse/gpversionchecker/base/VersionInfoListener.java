package com.robohorse.gpversionchecker.base;

import com.robohorse.gpversionchecker.domain.Version;

/**
 * Created by robohorse on 06.03.16.
 */
public interface VersionInfoListener {

    void onResulted(final Version version);

}
