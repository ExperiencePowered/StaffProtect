package net.experience.powered.staffprotect.records

import java.util.*

class Record(private var time: Long?, private var player: String?, private var actionType: ActionType?, private var content: String?) {

    override fun toString(): String {
        val format = RecordFile.dateFormat
        val time = format.format(time?.let { Date(it) })
        return "($time) [$player]: $content"
    }

    fun getTime(): Long {
        return time!!
    }

    fun getAction(): ActionType {
        return actionType!!
    }

    fun getPlayer(): String {
        return player!!
    }

    fun getContent(): String {
        return content!!
    }

    fun isValid(): Boolean {
        return !(time == null || player == null || content == null)
    }
}