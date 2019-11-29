package xyz.dev66.jumpropecounter.libs

import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun formatTime(duration: Duration): String {
    val minutes = "${duration.inSeconds.toLong() / 60}".padStart(2, '0')
    val seconds = "${duration.inSeconds.toLong() % 60}".padStart(2, '0')
    val milliseconds = "${(duration.inMilliseconds.toLong() % 1000) / 10}".padStart(2, '0')
    return "$minutes:$seconds.$milliseconds"
}

fun getRandomDate(): Date {
    val randomDate = Calendar.getInstance()
    randomDate.time = Date()
    randomDate.add(Calendar.YEAR, (-1..1).random())
    randomDate.add(Calendar.MONTH, (-12..12).random())
    randomDate.add(Calendar.DAY_OF_YEAR, (-30..30).random())
    return randomDate.time
}