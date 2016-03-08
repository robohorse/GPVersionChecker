package com.robohorse.gpversionchecker.domain;

/**
 * Created by robohorse on 06.03.16.
 */
public class Version {
    private String newVersionCode;
    private String changes;
    private boolean needToUpdate;

    public Version(String newVersionCode, String changes, boolean needToUpdate) {
        this.newVersionCode = newVersionCode;
        this.changes = changes;
        this.needToUpdate = needToUpdate;
    }

    public String getNewVersionCode() {
        return newVersionCode;
    }

    public String getChanges() {
        return changes;
    }

    public boolean isNeedToUpdate() {
        return needToUpdate;
    }
}
