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
    private String url;
    private String description;

    public Version(String newVersionCode, String changes, boolean needToUpdate, String url, String description) {
        this.newVersionCode = newVersionCode;
        this.changes = changes;
        this.needToUpdate = needToUpdate;
        this.url = url;
        this.description = description;
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

    public String getUrl() {
        return url == null ? "" : url;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    @Override
    public String toString() {
        final String newLine = System.getProperty("line.separator");

        return new StringBuilder()
                .append(newLine)
                .append("Google play app version: " + getNewVersionCode() + newLine)
                .append("url: " + getUrl() + newLine)
                .append("description: " + getDescription() + newLine)
                .append("with changes: " + getChanges() + newLine)
                .append("update required: " + isNeedToUpdate()).toString();
    }
}
