package com.robohorse.gpversionchecker.utils

import org.apache.commons.io.IOUtils
import java.io.IOException
import java.io.StringWriter

class AssetsReader {

    fun read(name: String) = try {
        readFile(name)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }

    private fun readFile(name: String): String {
        val inputStream = this.javaClass.classLoader!!.getResourceAsStream(name)
        val writer = StringWriter()
        IOUtils.copy(inputStream, writer, "UTF-8")
        IOUtils.closeQuietly(inputStream)
        return writer.toString()
    }
}
