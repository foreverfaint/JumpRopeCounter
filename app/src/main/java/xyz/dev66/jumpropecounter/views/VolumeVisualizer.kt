package xyz.dev66.jumpropecounter.views

import android.content.Context
import android.graphics.*
import android.icu.text.SimpleDateFormat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import xyz.dev66.jumpropecounter.libs.IRecorderVolumeListener
import xyz.dev66.jumpropecounter.libs.*
import java.lang.Exception
import java.util.*


private const val DEFAULT_NUM_COLUMNS = SECOND_COUNT_IN_VIEW * 1000 / SAMPLING_INTERVAL


class VolumeVisualizer @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0): View(context, attrs, defStyle), IRecorderVolumeListener {

    private val LOG_TAG: String = VolumeVisualizer::class.java.simpleName

    private val volumeCanvas by lazy {
        Canvas(cachedBitmap)
    }

    private val cachedBitmap by lazy {
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    private val defaultMatrix = Matrix()

    private val volumeQueue = LinkedList<Int>()

    private val columnWidth by lazy {
        width.toFloat() / DEFAULT_NUM_COLUMNS
    }

    private val volumeBackgroundPaint= Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }

    private val volumeForegroundPaint = Paint().apply {
        color = Color.DKGRAY
    }

    override fun onRestart() {
        volumeQueue.clear()
        volumeQueue.addAll(Array(DEFAULT_NUM_COLUMNS.toInt()) { 0 })

        try {
            volumeCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(LOG_TAG, "$e")
        }

        postInvalidate()
    }

    override fun onVolumeReceived(volume: Int) {
        Log.v(LOG_TAG, "volume=$volume, time=${SimpleDateFormat("HH:mm:ss.SSS").format(Date())}}")

        with (volumeQueue) {
            addLast(volume)
            removeFirst()
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

            for ((index, value) in volumeQueue.withIndex()) {
                val y = value * 1f / Byte.MAX_VALUE * height
                val y1 = height * 0.5f - y * 0.5f
                val y2 = height * 0.5f + y * 0.5f
                val x1 = index * columnWidth + columnWidth * 0.25f
                val x2 = (index + 1) * columnWidth - columnWidth * 0.25f
                drawRect(x1, y1, x2, y2, volumeForegroundPaint)
            }
        }

        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(cachedBitmap, defaultMatrix, null)
    }
}