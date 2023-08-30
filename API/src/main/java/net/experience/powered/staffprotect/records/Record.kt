package net.experience.powered.staffprotect.records

import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class Record(private var time: Long, player: String, content: String) {

    private var player: String? = player
    private var content: String? = content

    override fun toString(): String {
        val format = SimpleDateFormat("HH:mm:ss")
        val time = format.format(Date(time))
        return "($time) [$player]: $content"
    }

    fun write(file: File) {
        file.createNewFile()

        val list = ArrayList<String>()
        val reader = FileReader(file)
        reader.forEachLine { line ->
            list.add(line + "\n")
        }
        reader.close()
        val writer = FileWriter(file)
        list.forEach { line ->
            writer.write(line)
        }
        writer.write(toString())
        writer.close()
    }

    fun getPlayer(): String {
        return player!!
    }
}