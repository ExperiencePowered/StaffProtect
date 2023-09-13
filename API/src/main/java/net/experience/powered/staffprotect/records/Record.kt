package net.experience.powered.staffprotect.records

import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

class Record(private var time: Long?, private var player: String?, private var content: String?) {

    override fun toString(): String {
        val format = RecordFile.dateFormat
        val time = format.format(time?.let { Date(it) })
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

    fun isValid(): Boolean {
        return !(time == null || player == null || content == null)
    }
}