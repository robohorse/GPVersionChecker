package com.robohorse.gpversionchecker.helper;

import android.text.Html;
import android.text.TextUtils;

import com.robohorse.gpversionchecker.debug.ALog;
import com.robohorse.gpversionchecker.domain.Version;

import org.jsoup.nodes.Document;

/**
 * Created by vadim on 17.07.16.
 */
public class DataParser {
    private static final String DIV_VERSION = "div[itemprop=softwareVersion]";
    private static final String DIV_CHANGES = "div[class=recent-change]";
    private static final String DIV_DESCRIPTION = "div[itemprop=description]";

    public Version parse(Document document, final String currentVersion, final String url) {
        final String newVersion = document.select(DIV_VERSION)
                .first()
                .ownText();

        final String changes = String.valueOf(Html.fromHtml(document.select(DIV_CHANGES)
                .html()));

        final String description = String.valueOf(Html.fromHtml(document.select(DIV_DESCRIPTION)
                .html()));

        ALog.d("current version: " + currentVersion + "; google play version: " + newVersion);

        if (TextUtils.isEmpty(newVersion)
                || TextUtils.isEmpty(currentVersion)) {
            return null;
        }

        final int currentVersionValue = Integer.parseInt(replaceNonDigits(currentVersion));
        final int newVersionValue = Integer.parseInt(replaceNonDigits(newVersion));

        final boolean needToUpdate = newVersionValue > currentVersionValue;

        return new Version.Builder()
                .setNewVersionCode(newVersion)
                .setChanges(changes)
                .setNeedToUpdate(needToUpdate)
                .setUrl(url)
                .setDescription(description)
                .build();
    }

    private String replaceNonDigits(String value) {
        value = value.replaceAll("[^\\d.]", "");
        value = value.replace(".", "");
        return value;
    }
}
