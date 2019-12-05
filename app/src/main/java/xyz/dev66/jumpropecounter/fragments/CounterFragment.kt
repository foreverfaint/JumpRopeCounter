package xyz.dev66.jumpropecounter.fragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife

import xyz.dev66.jumpropecounter.R
import xyz.dev66.jumpropecounter.libs.*
import xyz.dev66.jumpropecounter.views.TimingAxis
import xyz.dev66.jumpropecounter.views.VolumeVisualizer
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


@ExperimentalTime
class CounterFragment(val counterListener: ICounterListener) : Fragment() {

    interface ICounterListener {
        fun onCounterCompleted(count: Int)
    }

    @BindView(R.id.tv_counter)
    lateinit var tvCounter: TextView

    @BindView(R.id.tv_starter)
    lateinit var tvStarter: TextView

    @BindView(R.id.v_volume_visualizer)
    lateinit var vVolumeView: VolumeVisualizer

    @BindView(R.id.v_timing_axis)
    lateinit var vTimingAxis: TimingAxis

    private val recorder by lazy {
        Recorder(VolumeCalculator(vVolumeView))
    }

    private val timerCounter by lazy {
        initTimerCounter()
    }

    private val timerStarter by lazy {
        initTimerStarter()
    }

    private fun initTimerStarter(): CountDownTimer {
        return object: CountDownTimer(STARTER_MILLIS_IN_FUTURE, STARTER_COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                tvStarter.textSize = 96 * ((1000 - millisUntilFinished % 1000) / 1000.0f + 1.0f)
                tvStarter.text = ((millisUntilFinished / 1000) + 1).toString()
            }

            override fun onFinish() {
                startCounterAfterStarterCompleted()
            }
        }
    }

    private fun initTimerCounter(): CountDownTimer {
        return object: CountDownTimer(COUNTER_MILLIS_IN_FUTURE, COUNTER_COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                val duration = millisUntilFinished.toDuration(TimeUnit.MILLISECONDS)
                tvCounter.text = formatTime(duration)
                vTimingAxis.onTick(millisUntilFinished)
            }

            override fun onFinish() {
                counterListener.onCounterCompleted(10)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_counter, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    private fun startStarter() {
        tvStarter.visibility = View.VISIBLE

        tvCounter.text = formatTime(COUNTER_MILLIS_IN_FUTURE.toDuration(TimeUnit.MILLISECONDS))
        tvCounter.visibility = View.INVISIBLE

        timerStarter.start()
    }

    fun startCounterAfterStarterCompleted() {
        vVolumeView.onRestart()

        tvStarter.visibility = View.INVISIBLE

        tvCounter.text = formatTime(COUNTER_MILLIS_IN_FUTURE.toDuration(TimeUnit.MILLISECONDS))
        tvCounter.visibility = View.VISIBLE

        timerCounter.start()

        recorder.start()
    }

    private fun stopCounter() {
        tvStarter.visibility = View.INVISIBLE

        timerStarter.cancel()

        timerCounter.cancel()

        recorder.stop()

        vVolumeView.onRestart()
    }

    fun start() {
        startStarter()
    }

    fun stop() {
        stopCounter()
    }
}
