package com.robohorse.gpversionchecker.domain;

import java.io.Serializable;

/**
 * Created by robohorse on 06.03.16.
 */
public class Version implements Serializable {
    private static final String SEPARATOR = System.getProperty("line.separator");
    private static final long serialVersionUID = 2635L;
    private String newVersionCode;
    private String changes;
    private boolean needToUpdate;
    private String url;
    private String description;

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

    public static class Builder {
        private Version instance;

        public Builder() {
            instance = new Version();
        }

        public Builder setNewVersionCode(String newVersionCode) {
            instance.newVersionCode = newVersionCode;
            return this;
        }

        public Builder setChanges(String changes) {
            instance.changes = changes;
            return this;
        }

        public Builder setNeedToUpdate(boolean needToUpdate) {
            instance.needToUpdate = needToUpdate;
            return this;
        }

        public Builder setUrl(String url) {
            instance.url = url;
            return this;
        }

        public Builder setDescription(String description) {
            instance.description = description;
            return this;
        }

        public Version build() {
            return instance;
        }
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(SEPARATOR)
                .append("Google play app version: " + getNewVersionCode() + SEPARATOR)
                .append("url: " + getUrl() + SEPARATOR)
                .append("description: " + getDescription() + SEPARATOR)
                .append("with changes: " + getChanges() + SEPARATOR)
                .append("update required: " + isNeedToUpdate()).toString();
    }
}
