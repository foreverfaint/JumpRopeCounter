package xyz.dev66.jumpropecounter.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import xyz.dev66.jumpropecounter.libs.formatTime
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


const val PRIMARY_TICK_COUNT = 7

const val SECONDARY_TICK_COUNT = 4

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

    fun onTick(millisUntilFinished: Long) {
        with (axisCanvas) {
            drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            val offset = millisUntilFinished % 1000
            val totalTickCount = PRIMARY_TICK_COUNT * SECONDARY_TICK_COUNT
            val stepFor250ms = width.toFloat() / totalTickCount
            val stepFor1sec = stepFor250ms * 4
            for (i in 0 until totalTickCount * 2) {
                val x1 = stepFor250ms * i + (offset / 1000f - 1f) * stepFor1sec
                if (x1 < 0) continue
                if (x1 > width) continue
                if (i % SECONDARY_TICK_COUNT == 0) {
                    val y1 = height * 0.5f
                    val y2 = height.toFloat()
                    drawLine(x1, y1, x1, y2, axisPrimaryPaint)

                    val tickMillis = (60 - millisUntilFinished / 1000 + i / SECONDARY_TICK_COUNT) * 1000
                    val label = formatTime(tickMillis.toDuration(TimeUnit.MILLISECONDS), useMs = false)
                    val labelWidth = axisTextPaint.measureText(label)
                    val x3 = x1 - labelWidth * 0.5f
                    val y3 = y1 - 5f
                    Log.v(LOG_TAG, "$millisUntilFinished: i=$i, pos=($x3, $y3), label=$label")
                    drawText(label, x3, y3, axisTextPaint)
                } else {
                    val y1 = height * 0.75f
                    val y2 = height.toFloat()
                    drawLine(x1, y1, x1, y2, axisSecondaryPaint)
                }
            }
        }

        postInvalidate()
    }
}