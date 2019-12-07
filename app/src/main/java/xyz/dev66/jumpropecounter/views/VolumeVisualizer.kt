package xyz.dev66.jumpropecounter.views

import android.content.Context
import android.graphics.*
import android.icu.text.SimpleDateFormat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import xyz.dev66.jumpropecounter.libs.*
import java.lang.Exception
import java.util.*

class VolumeVisualizer @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0): View(context, attrs, defStyle) {

    private val LOG_TAG: String = VolumeVisualizer::class.java.simpleName

    private val volumeCanvas by lazy {
        Canvas(cachedBitmap)
    }

    private val cachedBitmap by lazy {
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    private val defaultMatrix = Matrix()

    private val volumeWindow = LinkedList<Pair<Int, Long>>()

    private val volumeBackgroundPaint= Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }

    private val volumeForegroundPaint = Paint().apply {
        color = Color.DKGRAY
    }

    fun reset() {
        volumeWindow.clear()

        try {
            volumeCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(LOG_TAG, "$e")
        }

        postInvalidate()
    }

    fun receive(volume: Int, lastInMillis: Long) {
        Log.v(LOG_TAG, "volume=$volume, time=${SimpleDateFormat("HH:mm:ss.SSS").format(Date())}}")

        val finishedInMillis = COUNTER_MILLIS_IN_FUTURE - lastInMillis
        val firstInMillis = addVolumeToVolumeWindow(volume, finishedInMillis)

        trimVolumeWindow(firstInMillis)

        if (width <= 0 || height <= 0) {
            return
        }

        with (volumeCanvas) {
            drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            drawRect(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                volumeBackgroundPaint
            )

            for ((currentVolume, currentInMillis) in volumeWindow) {
                val y = currentVolume * 1f / Byte.MAX_VALUE * height
                val y1 = height * 0.5f - y * 0.5f
                val y2 = height * 0.5f + y * 0.5f
                val x = width.toFloat() * (1f - (finishedInMillis - currentInMillis) / MILLISECONDS_IN_VIEW)
                val x1 = x - 2f
                val x2 = x + 2f
                drawRect(x1, y1, x2, y2, volumeForegroundPaint)
            }
        }

        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width > 0 && height > 0) {
            canvas.drawBitmap(cachedBitmap, defaultMatrix, null)
        }
    }

    private fun addVolumeToVolumeWindow(volume: Int, finishedInMillis: Long): Long {
        volumeWindow.addLast(Pair(volume, finishedInMillis))
        Log.d(LOG_TAG, "finishedInMillis=$finishedInMillis")
        return finishedInMillis - MILLISECONDS_IN_VIEW.toLong()
    }

    private fun trimVolumeWindow(firstInMillis: Long) {
        with (volumeWindow) {
            while (true) {
                val (_, currentInMillis) = first
                if (currentInMillis < firstInMillis) {
                    removeFirst()
                    if (size == 0) {
                        break
                    }
                    continue
                }
                break
            }
        }
    }
}