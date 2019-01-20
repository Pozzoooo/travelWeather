package pozzo.apps.travelweather.core

import java.io.File

class FileLoader(val filePath: String) {

    fun string() : String {
        val fileUrl = javaClass.classLoader!!.getResource(filePath)!!
        return File(fileUrl.toURI()).readText()
    }
}
