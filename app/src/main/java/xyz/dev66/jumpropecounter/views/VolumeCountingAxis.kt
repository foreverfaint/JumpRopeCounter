package xyz.dev66.jumpropecounter.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import xyz.dev66.jumpropecounter.libs.*
import java.lang.Exception
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt
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

    private val patternWindow = LinkedList<Pair<Int, Long>>()

    private val recognizerWindow = LinkedList<Pair<Int, Long>>()

    private val windowSizeInMillis = 400L

    private var lastCount = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width > 0 && height > 0) {
            canvas.drawBitmap(cachedBitmap, defaultMatrix, null)
        }
    }

    fun receive(volume: Int, millisUntilFinished: Long) {
        val millisFinished = COUNTER_MILLIS_IN_FUTURE - millisUntilFinished

        updateRecognizerWindow(volume, millisFinished)

        if (containsPattern()) {
            lastCount += 1
            patternWindow.addLast(Pair(lastCount, millisFinished))
        }

        val firstInMillis = millisFinished - MILLISECONDS_IN_VIEW.toLong()
        trimPatternWindow(firstInMillis)

        if (width <= 0 || height <= 0) {
            return
        }

        with (axisCanvas) {
            drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            for ((patternCount, currentInMillis) in patternWindow) {
                val x1 = width.toFloat() * (1f - (millisFinished - currentInMillis) * 1f / MILLISECONDS_IN_VIEW)
                val y1 = 0f
                val y2 = height * 0.5f
                drawLine(x1, y1, x1, y2, axisPrimaryPaint)

                val labelText = patternCount.toString()
                val labelWidth = axisTextPaint.measureText(labelText)
                val x2 = x1 - labelWidth * 0.5f
                drawText(labelText, x2, y2 + 30f, axisTextPaint)
            }
        }

        postInvalidate()
    }

    fun reset() {
        lastCount = 0
        recognizerWindow.clear()
        patternWindow.clear()

        try {
            axisCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(LOG_TAG, "$e")
        }

        postInvalidate()
    }

    private fun updateRecognizerWindow(volume: Int, millisFinished: Long) {
        with (recognizerWindow) {
            addLast(Pair(volume, millisFinished))

            while (true) {
                if (size == 0) {
                    break
                }

                val (_, currentInMillis) = first
                if (currentInMillis + windowSizeInMillis < millisFinished) {
                    removeFirst()
                    continue
                }
                break
            }
        }
    }

    private fun containsPattern(): Boolean {
        Log.d(LOG_TAG, recognizerWindow.joinToString { it.first.toString() })

        if (recognizerWindow.size >= 4) {
            val (lastVolume, _) = recognizerWindow.last
            for ((i, p) in recognizerWindow.withIndex()) {
                val (volume, _) = p

                if (i == recognizerWindow.size - 1) {
                    return true
                } else if (lastVolume < volume) {
                    break
                } else if (volume > 0 && lastVolume / volume < 3) {
                    return false
                }
            }
        }

        return false
    }

    private fun trimPatternWindow(firstInMillis: Long) {
        with (patternWindow) {
            while (true) {
                if (size == 0) {
                    break
                }

                val (_, currentInMillis) = first
                if (currentInMillis < firstInMillis) {
                    removeFirst()
                    continue
                }
                break
            }
        }
    }
}