package xyz.dev66.jumpropecounter.libs

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlin.math.log10

class Recorder {
    private val LOG_TAG: String = Recorder::class.java.simpleName

    private val bufferSizeInBytes: Int = AudioRecord.getMinBufferSize(
        SAMPLING_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private val audioRecord: AudioRecord by lazy {
        AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            SAMPLING_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSizeInBytes)
    }

    fun readVolume(): Int {
        // use a double size short array.
        val audioBuffer = ShortArray(bufferSizeInBytes)
        val readSize = audioRecord.read(audioBuffer, 0, audioBuffer.size)
        return calculateVolume(audioBuffer, readSize)
    }

    fun start() {
        Log.v(LOG_TAG, "Recording start")
        audioRecord.startRecording()
    }

    fun stop() {
        audioRecord.stop()
        Log.v(LOG_TAG, "Recording stopped")
    }

    private fun calculateVolume(sampleData: ShortArray, readSize: Int): Int =
        10 * log10(sampleData.take(readSize).map { it.toFloat() }.map { it * it }.average()).toInt()
}

