package xyz.dev66.jumpropecounter.libs

import android.icu.text.SimpleDateFormat
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.util.*
import kotlin.math.log10

interface IRecorderDataListener {
    fun onRestart()
    fun onDataReceived(sampleData: ShortArray, readSize: Int)
}

interface IRecorderVolumeListener {
    fun onRestart()
    fun onVolumeReceived(volume: Int)
}

class VolumeCalculator(private val listener: IRecorderVolumeListener): IRecorderDataListener {
    override fun onRestart() =
        listener.onRestart()

    override fun onDataReceived(sampleData: ShortArray, readSize: Int) {
        var sum = 0.0
        for (i in 0 until readSize) {
            sum += sampleData[i] * sampleData[i]
        }
        listener.onVolumeReceived(10 * log10(sum / readSize).toInt())
    }

}

class Recorder(private val listener: IRecorderDataListener) {

    private val LOG_TAG: String = Recorder::class.java.simpleName

    private var timer: Timer? = null

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

    private var isRecording = false

    private fun onStart() {
        Log.v(LOG_TAG, "Recording start")
        audioRecord.startRecording()

        isRecording = true

        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                if (!isRecording) {
                    audioRecord.stop()
                    // Must stop AudioRecord to release recording resource before timer is cancelled
                    timer!!.cancel()
                    Log.v(LOG_TAG, "Recording stopped")
                    return
                }

                // use a double size short array.
                val audioBuffer = ShortArray(bufferSizeInBytes)
                val readSize = audioRecord.read(audioBuffer, 0, audioBuffer.size)
                Log.v(LOG_TAG, "readSize=$readSize, " +
                        "size=${audioBuffer.size}, " +
                        "time=${SimpleDateFormat("HH:mm:ss.SSS").format(Date())}")

                listener.onDataReceived(audioBuffer, readSize)
            }
        }, 0L, SAMPLING_INTERVAL)
    }

    private fun onStop() {
        isRecording = false
    }

    fun start() {
        onStart()
    }

    fun stop() {
        onStop()
    }
}

