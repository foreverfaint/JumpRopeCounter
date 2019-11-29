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
        var actual = formatTime(1000.toDuration(TimeUnit.MILLISECONDS))
        var expected = "00:01.00"
        assertEquals(expected, actual)
    }

    @ExperimentalTime
    @Test
    fun format1Minute() {
        var actual = formatTime(60000.toDuration(TimeUnit.MILLISECONDS))
        var expected = "01:00.00"
        assertEquals(expected, actual)
    }

    @ExperimentalTime
    @Test
    fun format0Second() {
        var actual = formatTime(0.toDuration(TimeUnit.MILLISECONDS))
        var expected = "00:00.00"
        assertEquals(expected, actual)
    }

    @ExperimentalTime
    @Test
    fun formatLessThan100Ms() {
        var actual = formatTime(59.toDuration(TimeUnit.MILLISECONDS))
        var expected = "00:00.05"
        assertEquals(expected, actual)
    }

    @ExperimentalTime
    @Test
    fun formatLessThan1000Ms() {
        var actual = formatTime(960.toDuration(TimeUnit.MILLISECONDS))
        var expected = "00:00.96"
        assertEquals(expected, actual)
    }
}