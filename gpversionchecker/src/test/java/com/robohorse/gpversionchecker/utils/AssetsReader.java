package com.robohorse.gpversionchecker.utils;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Created by vadim on 17.07.16.
 */
public class AssetsReader {

    public String read(String name) throws IOException, NullPointerException {
        String content = null;
        try {
            content = readFile(name);
        } catch (IOException | NullPointerException e) {
            Assert.fail(name + " not found");
        }

        Assert.assertNotNull("Content is empty", content);
        return content;
    }

    private String readFile(String name) throws IOException, NullPointerException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(name);
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        IOUtils.closeQuietly(inputStream);
        return writer.toString();
    }
}
