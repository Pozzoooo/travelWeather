package pozzo.apps.travelweather.core

import java.nio.file.Files
import java.nio.file.Paths

class FileLoader(val filePath: String) {
    var fileBytes : ByteArray = ByteArray(0)

    fun read() = apply {
        val fileUrl = javaClass.classLoader!!.getResource(filePath)!!
        fileBytes = Files.readAllBytes(Paths.get(fileUrl.toURI()))
    }

    fun string() = String(fileBytes)
}
