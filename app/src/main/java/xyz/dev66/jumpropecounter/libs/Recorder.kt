package xyz.dev66.jumpropecounter.libs

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Process
import android.util.Log
import kotlin.concurrent.thread
import kotlin.math.abs

const val SAMPLE_RATE = 44100

interface IRecorderDataListener {
    fun reset()
    fun onDataReceived(sampleData: ByteArray)
}

interface IRecorderVolumeListener {
    fun reset()
    fun onVolumeReceived(volume: Int)
}

class VolumeCalculator(private val listener: IRecorderVolumeListener): IRecorderDataListener {
    override fun reset() =
        listener.reset()

    override fun onDataReceived(sampleData: ByteArray) =
        listener.onVolumeReceived(sampleData.map { abs(it.toInt()) }.average().toInt())
}

class Recorder(private val listener: IRecorderDataListener) {
    private val LOG_TAG: String = Recorder::class.java.simpleName

    private var bufferSizeInBytes: Int = AudioRecord.getMinBufferSize(
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var isRecording = false

    private fun onStart() {
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSizeInBytes
        )

        audioRecord.startRecording()
        Log.v(LOG_TAG, "Recording start")

        isRecording = true

        val sampleData = ByteArray(bufferSizeInBytes)

        listener.reset()
        while (isRecording) {
            audioRecord.read(sampleData, 0, sampleData.size)
            listener.onDataReceived(sampleData)
        }

        audioRecord.stop()
        audioRecord.release()
        Log.v(LOG_TAG, "Recording stopped")
    }

    private fun onStop() {
        isRecording = false
    }

    fun start() {
        thread(start = true, priority = Process.THREAD_PRIORITY_AUDIO) {
            onStart()
        }
    }

    fun stop() {
        onStop()
    }
}

