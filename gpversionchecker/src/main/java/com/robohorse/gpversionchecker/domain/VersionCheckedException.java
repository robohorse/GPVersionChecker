package com.robohorse.gpversionchecker.domain;

/**
 * Created by v.shchenev on 05/10/2017.
 */

public class VersionCheckedException extends Exception {
    public VersionCheckedException() {
        super("Version already checked");
    }
}
