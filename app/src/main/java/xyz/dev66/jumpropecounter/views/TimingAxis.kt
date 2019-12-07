package xyz.dev66.jumpropecounter.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import xyz.dev66.jumpropecounter.libs.*
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
class TimingAxis @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0): View(context, attrs, defStyle) {

    private val LOG_TAG: String = TimingAxis::class.java.simpleName

    private val defaultMatrix = Matrix()

    private val axisCanvas by lazy {
        Canvas(cachedBitmap)
    }

    private val cachedBitmap by lazy {
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    private val axisPrimaryPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2.5f
    }

    private val axisTextPaint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
        typeface = Typeface.DEFAULT
    }

    private val axisSecondaryPaint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 2.5f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(cachedBitmap, defaultMatrix, null)
    }

    fun receive(millisUntilFinished: Long) {
        with (axisCanvas) {
            drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            val millisFinished = COUNTER_MILLIS_IN_FUTURE - millisUntilFinished
            val offset = millisFinished % 1000
            val widthForOneSecond = width.toFloat() / SECOND_COUNT_IN_VIEW
            val widthForQuarterSecond: Float = widthForOneSecond / QUARTER_SECOND_COUNT
            val widthForOffset = offset / 1000f * widthForOneSecond
            for (i in -QUARTER_SECOND_COUNT until TOTAL_QUARTER_SECOND_COUNT + QUARTER_SECOND_COUNT step 1) {
                // set x axis zero point to the right end
                val x1 = width - i * widthForQuarterSecond - widthForOffset
                if (x1 < 0) {
                    break
                } else if (x1 > width) {
                    continue
                } else if (i % QUARTER_SECOND_COUNT == 0) {
                    val y1 = height * 0.5f
                    val y2 = height.toFloat()
                    drawLine(x1, y1, x1, y2, axisPrimaryPaint)
                } else {
                    val y1 = height * 0.75f
                    val y2 = height.toFloat()
                    drawLine(x1, y1, x1, y2, axisSecondaryPaint)
                }
            }

            val secondsFinished = millisFinished / 1000
            var i = 0
            for (j in secondsFinished downTo max(0, secondsFinished - SECOND_COUNT_IN_VIEW)) {
                val x1 = width - i * widthForOneSecond - widthForOffset
                if (x1 < 0) {
                    break
                } else {
                    val labelText = formatTime(j.toDuration(TimeUnit.SECONDS), useMs = false)
                    val labelWidth = axisTextPaint.measureText(labelText)
                    val x2 = x1 - labelWidth * 0.5f
                    val y1 = height * 0.5f - 5f
                    drawText(labelText, x2, y1, axisTextPaint)
                }
                i += 1
            }
        }

        postInvalidate()
    }

    fun reset() {
        try {
            axisCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(LOG_TAG, "$e")
        }

        postInvalidate()
    }
}