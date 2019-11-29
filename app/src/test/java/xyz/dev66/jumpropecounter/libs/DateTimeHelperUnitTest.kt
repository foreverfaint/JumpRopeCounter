package xyz.dev66.jumpropecounter.libs

import org.junit.Test

import org.junit.Assert.*
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

class DateTimeHelperUnitTest {
    @ExperimentalTime
    @Test
    fun format1Second() {
        val actual = formatTime(1000.toDuration(TimeUnit.MILLISECONDS))
        val expected = "00:01.00"
        assertEquals(expected, actual)
    }

    @ExperimentalTime
    @Test
    fun format1Minute() {
        val actual = formatTime(60000.toDuration(TimeUnit.MILLISECONDS))
        val expected = "01:00.00"
        assertEquals(expected, actual)
    }

    @ExperimentalTime
    @Test
    fun format0Second() {
        val actual = formatTime(0.toDuration(TimeUnit.MILLISECONDS))
        val expected = "00:00.00"
        assertEquals(expected, actual)
    }

    @ExperimentalTime
    @Test
    fun formatLessThan100Ms() {
        val actual = formatTime(59.toDuration(TimeUnit.MILLISECONDS))
        val expected = "00:00.05"
        assertEquals(expected, actual)
    }

    @ExperimentalTime
    @Test
    fun formatLessThan1000Ms() {
        val actual = formatTime(960.toDuration(TimeUnit.MILLISECONDS))
        val expected = "00:00.96"
        assertEquals(expected, actual)
    }
}