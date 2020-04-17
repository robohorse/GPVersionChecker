package com.robohorse.gpversionchecker

import com.robohorse.gpversionchecker.utils.AssetsReader
import com.robohorse.gpversionchecker.utils.DataParser
import com.robohorse.gpversionchecker.utils.TextFormatter
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class DataParserTest {
    private val assetsReader = AssetsReader()

    @RelaxedMockK
    lateinit var textFormatError: TextFormatter

    @InjectMockKs
    lateinit var dataParser: DataParser

    @Test
    fun testDataParsing_isCorrectChangesReading() {
        val htmlBody = assetsReader.read("sample_app.html")
        val document = Jsoup.parse(htmlBody)
        every { textFormatError.format("design") }.returns("design")
        val version = dataParser.parse(document, URL)
        assertNotNull(version, "VersionVO is empty")
        assertEquals("design", version.changes, "App changes was not parsed correct")
    }

    @Test
    fun testDataParsing_isCorrectDescriptionReading() {
        val htmlBody = assetsReader.read("sample_app.html")
        val document = Jsoup.parse(htmlBody)
        every { textFormatError.format("Awesome app") }.returns("Awesome app")
        val version = dataParser.parse(document, URL)
        assertNotNull(version, "VersionVO is empty")
        assertEquals("Awesome app", version.description, "App description was not parsed correct")
    }

    @Test
    fun testDataParsing_isCorrectChangesReadingWithCharacters() {
        val htmlBody = assetsReader.read("sample_app_symbol_version.html")
        val document = Jsoup.parse(htmlBody)
        every { textFormatError.format("design") }.returns("design")
        val version = dataParser.parse(document, URL)
        assertNotNull(version, "VersionVO is empty")
        assertEquals("design", version.changes, "App changes was not parsed correct")
    }

    @Test
    fun testDataParsing_isCorrectDescriptionReadingWithCharacters() {
        val htmlBody = assetsReader.read("sample_app_symbol_version.html")
        val document = Jsoup.parse(htmlBody)
        every { textFormatError.format("Awesome app") }.returns("Awesome app")
        val version = dataParser.parse(document, URL)
        assertNotNull(version, "VersionVO is empty")
        assertEquals("Awesome app", version.description, "App description was not parsed correct")
    }
}

private const val URL = "https://play.google.com/store/apps/details?id=com.robohorse.gpversionchecker"
