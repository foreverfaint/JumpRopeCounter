package xyz.dev66.jumpropecounter.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import xyz.dev66.jumpropecounter.R
import kotlin.time.ExperimentalTime


@ExperimentalTime
class VolumeVisualizerLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0): LinearLayout(context, attrs, defStyle) {

    private val LOG_TAG: String = VolumeVisualizerLayout::class.java.simpleName

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.volume_visualizer_layout, this)
        ButterKnife.bind(this)
    }

    @BindView(R.id.v_volume_visualizer)
    lateinit var vVolumeView: VolumeVisualizer

    @BindView(R.id.v_timing_axis)
    lateinit var vTimingAxis: TimingAxis

    @BindView(R.id.v_volume_counting_axis)
    lateinit var vVolumeCountingAxis: VolumeCountingAxis

    fun reset() {
        vVolumeView.reset()
        vTimingAxis.reset()
        vVolumeCountingAxis.reset()
    }

    fun receive(volume: Int, millisUntilFinished: Long) {
        vVolumeView.receive(volume, millisUntilFinished)
        vTimingAxis.receive(millisUntilFinished)
        vVolumeCountingAxis.receive(volume, millisUntilFinished)
    }
}