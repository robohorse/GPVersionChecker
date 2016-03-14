package com.robohorse.gpversionchecker.domain;

import java.io.Serializable;

/**
 * Created by robohorse on 06.03.16.
 */
public class Version implements Serializable {
    private static final long serialVersionUID = 2635L;
    private String newVersionCode;
    private String changes;
    private boolean needToUpdate;

    public Version(String newVersionCode, String changes, boolean needToUpdate) {
        this.newVersionCode = newVersionCode;
        this.changes = changes;
        this.needToUpdate = needToUpdate;
    }

    public String getNewVersionCode() {
        return newVersionCode == null ? "" : newVersionCode;
    }

    public String getChanges() {
        return changes == null ? "" : changes;
    }

    public boolean isNeedToUpdate() {
        return needToUpdate;
    }

    @Override
    public String toString() {
        final String newLine = System.getProperty("line.separator");

        return new StringBuilder()
                .append(newLine)
                .append("Google play app version: " + getNewVersionCode() + newLine)
                .append("with changes: " + getChanges() + newLine)
                .append("update required: " + isNeedToUpdate()).toString();
    }
}
