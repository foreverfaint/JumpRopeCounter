package xyz.dev66.jumpropecounter.fragments

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import xyz.dev66.jumpropecounter.libs.IRecorderVolumeListener
import java.util.*


private const val DEFAULT_NUM_COLUMNS = 200

private const val HEADER_HEIGHT_PERCENT = 0.2f

private const val FOOTER_HEIGHT_PERCENT = 0.2f


class VolumeVisualizer @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0): View(context, attrs, defStyle), IRecorderVolumeListener {

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

    private val axisForegroundPaint = Paint().apply {
        color = Color.BLACK
    }

    private val axisBackgroundPaint = Paint().apply {
        color = Color.TRANSPARENT
        style = Paint.Style.FILL
    }

    private val volumeBackgroundPaint= Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }

    private val volumeForegroundPaint = Paint().apply {
        color = Color.DKGRAY
    }

    override fun reset() {
        volumeQueue.clear()
        volumeQueue.addAll(Array(DEFAULT_NUM_COLUMNS) { 0 })
        volumeCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    override fun onVolumeReceived(volume: Int) {
        volumeQueue.addLast(volume)
        volumeQueue.removeFirst()

        volumeCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        drawHeader()
        drawFooter()
        drawBaseline()
        drawVolumes()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(cachedBitmap, defaultMatrix, null)
    }

    private fun drawHeader() {
        volumeCanvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            height * HEADER_HEIGHT_PERCENT,
            axisBackgroundPaint)
    }

    private fun drawFooter() {
        volumeCanvas.drawRect(
            0f,
            height * (1f - FOOTER_HEIGHT_PERCENT),
            width.toFloat(),
            height.toFloat(),
            axisBackgroundPaint)
    }

    private fun drawBaseline() {
        volumeCanvas.drawLines(
            floatArrayOf(0f, height * 0.5f, width.toFloat(), height * 0.5f),
            volumeForegroundPaint)
    }

    private fun drawVolumes() {
        val h = height * (1f - HEADER_HEIGHT_PERCENT - FOOTER_HEIGHT_PERCENT)
        volumeCanvas.drawRect(
            0f,
            height * HEADER_HEIGHT_PERCENT,
            width.toFloat(),
            height * (1 - FOOTER_HEIGHT_PERCENT),
            volumeBackgroundPaint
        )

        for ((index, value) in volumeQueue.withIndex()) {
            val y = value * 1f / Byte.MAX_VALUE * h
            val y1 = height * HEADER_HEIGHT_PERCENT + h * 0.5f - y * 0.5f
            val y2 = height * HEADER_HEIGHT_PERCENT + h * 0.5f + y * 0.5f
            val x1 = index * columnWidth + columnWidth * 0.25f
            val x2 = (index + 1) * columnWidth - columnWidth * 0.25f
            volumeCanvas.drawRect(x1, y1, x2, y2, volumeForegroundPaint)
        }
    }
}