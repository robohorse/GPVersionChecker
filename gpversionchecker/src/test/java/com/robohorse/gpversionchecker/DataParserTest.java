package com.robohorse.gpversionchecker;

import com.robohorse.gpversionchecker.domain.Version;
import com.robohorse.gpversionchecker.utils.AssetsReader;
import com.robohorse.gpversionchecker.utils.DataParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Created by vadim on 17.07.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DataParserTest {
    private static final String URL = "https://play.google.com/store/apps/details?id=com.robohorse.gpversionchecker";
    private AssetsReader assetsReader = new AssetsReader();
    private DataParser dataParser = new DataParser();

    @Test
    public void testDataParsing_isCorrectChangesReading() throws Exception {
        final String htmlBody = assetsReader.read("sample_app.html");
        final Document document = Jsoup.parse(htmlBody);
        final Version version = dataParser.parse(document, URL);

        assertNotNull("VersionVO is empty", version);
        assertTrue("App changes was not parsed correct",
                "design".equals(version.getChanges()));
    }

    @Test
    public void testDataParsing_isCorrectDescriptionReading() throws Exception {
        final String htmlBody = assetsReader.read("sample_app.html");
        final Document document = Jsoup.parse(htmlBody);
        final Version version = dataParser.parse(document, URL);

        assertNotNull("VersionVO is empty", version);
        assertTrue("App description was not parsed correct",
                "Awesome app".equals(version.getDescription()));
    }

    @Test
    public void testDataParsing_isCorrectChangesReadingWithCharacters() throws Exception {
        final String htmlBody = assetsReader.read("sample_app_symbol_version.html");
        final Document document = Jsoup.parse(htmlBody);
        final Version version = dataParser.parse(document, URL);

        assertNotNull("VersionVO is empty", version);
        assertTrue("App changes was not parsed correct",
                "design".equals(version.getChanges()));
    }

    @Test
    public void testDataParsing_isCorrectDescriptionReadingWithCharacters() throws Exception {
        final String htmlBody = assetsReader.read("sample_app_symbol_version.html");
        final Document document = Jsoup.parse(htmlBody);
        final Version version = dataParser.parse(document, URL);

        assertNotNull("VersionVO is empty", version);
        assertTrue("App description was not parsed correct",
                "Awesome app".equals(version.getDescription()));
    }
}

