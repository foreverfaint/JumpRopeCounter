package xyz.dev66.jumpropecounter.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import xyz.dev66.jumpropecounter.libs.*
import java.lang.Exception
import kotlin.math.max
import kotlin.time.ExperimentalTime

@ExperimentalTime
class VolumeCountingAxis @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0): View(context, attrs, defStyle) {

    private val LOG_TAG: String = VolumeCountingAxis::class.java.simpleName

    private val defaultMatrix = Matrix()

    private val axisCanvas by lazy {
        Canvas(cachedBitmap)
    }

    private val cachedBitmap by lazy {
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    private val axisPrimaryPaint = Paint().apply {
        color = Color.RED
        strokeWidth = 2.5f
    }

    private val axisTextPaint = Paint().apply {
        color = Color.LTGRAY
        textSize = 30f
        typeface = Typeface.DEFAULT
    }

    private var recognizer = PeakRecognizer()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width > 0 && height > 0) {
            canvas.drawBitmap(cachedBitmap, defaultMatrix, null)
        }
    }

    fun receive(volume: Int, millisUntilFinished: Long): Int {
        if (width <= 0 || height <= 0) {
            return 0
        }

        val millisFinished = COUNTER_MILLIS_IN_FUTURE - millisUntilFinished

        recognizer.add(volume, millisFinished)

        var totalPeakCount = 0

        with (axisCanvas) {
            drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            for ((volumeData, index) in recognizer.computePeaks(
                millisFinished - MILLISECONDS_IN_VIEW.toLong(), millisFinished)) {
                val x1 = width.toFloat() * (1f - (millisFinished - volumeData.currentInMillis) * 1f / MILLISECONDS_IN_VIEW)
                val y1 = 0f
                val y2 = height * 0.5f
                drawLine(x1, y1, x1, y2, axisPrimaryPaint)

                val labelText = index.toString()
                val labelWidth = axisTextPaint.measureText(labelText)
                val x2 = x1 - labelWidth * 0.5f
                drawText(labelText, x2, y2 + 30f, axisTextPaint)

                totalPeakCount = max(totalPeakCount, index)
            }
        }

        postInvalidate()

        return totalPeakCount
    }

    fun reset() {
        recognizer = PeakRecognizer()

        try {
            axisCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(LOG_TAG, "$e")
        }

        postInvalidate()
    }
}