package com.robohorse.gpversionchecker.utils;

import android.text.Html;
import android.text.TextUtils;

import com.robohorse.gpversionchecker.domain.Version;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by vadim on 17.07.16.
 */
public class DataParser {
    private static final String DIV_VERSION = "Current Version";
    private static final String DIV_CHANGES = "div[class=DWPxHb]";
    private static final String DIV_DESCRIPTION = "div[itemprop=description]";

    public Version parse(Document document, final String url) {
        String newVersion = "";
        if (document != null) {
            Elements elements = document.getElementsContainingOwnText(DIV_VERSION);
            for (Element element : elements) {
                if (element.siblingElements() != null) {
                    Elements subElements = element.siblingElements();
                    for (Element subElement : subElements) {
                        newVersion = subElement.text();
                    }
                }
            }
        }

        final String description = String.valueOf(Html.fromHtml(document.select(DIV_DESCRIPTION)
                .html()));

        String changes = null;
        final Elements elements = document.select(DIV_CHANGES);
        if (null != elements) {
            final Elements changesElements = elements.select(DIV_CHANGES);
            if (!changesElements.isEmpty()) {
                changes = String.valueOf(Html.fromHtml(changesElements.last().html()));
                if (TextUtils.equals(changes, description)) {
                    changes = null;
                }
            }
        }
        return new Version.Builder()
                .setNewVersionCode(newVersion)
                .setChanges(changes)
                .setUrl(url)
                .setDescription(description)
                .build();
    }

    public static String replaceNonDigits(String value) {
        value = value.replaceAll("[^\\d.]", "");
        value = value.replace(".", "");
        return value;
    }
}
