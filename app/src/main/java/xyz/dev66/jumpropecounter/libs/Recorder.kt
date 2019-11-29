package xyz.dev66.jumpropecounter.libs

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Process.THREAD_PRIORITY_AUDIO
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.concurrent.thread

const val SAMPLE_RATE = 44100

interface IRecordingDataReceivedListener {
    fun onDataReceived(data: ShortArray)
}

interface IRecorder {
    fun start()
    fun stop()
}

class Recorder(private val listener: IRecordingDataReceivedListener): IRecorder {

    private val LOG_TAG: String = Recorder::class.java.simpleName

    private var bufferSize: Int = AudioRecord.getMinBufferSize(
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var isRecording = false

    override fun start() {
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord.startRecording()
        Log.v(LOG_TAG, "Start recording")

        isRecording = true

        var shortsRead: Long = 0
        val audioBuffer = ShortArray(bufferSize / 2)
        while (isRecording) {
            val numberOfShort = audioRecord.read(audioBuffer, 0, audioBuffer.size)
            shortsRead += numberOfShort.toLong()
            listener.onDataReceived(audioBuffer)
        }

        audioRecord.stop()
        audioRecord.release()
        Log.v(LOG_TAG, "Recording stopped")
    }

    override fun stop() {
        isRecording = false
    }
}

class AsyncRecorder(listener: IRecordingDataReceivedListener) : IRecorder {
    private val recorder: IRecorder = Recorder(listener)

    override fun start() {
        thread(start = true, priority = THREAD_PRIORITY_AUDIO) {
            recorder.start()
        }
    }

    override fun stop() {
        recorder.stop()
    }
}