package xyz.dev66.jumpropecounter.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife

import xyz.dev66.jumpropecounter.R
import xyz.dev66.jumpropecounter.libs.*
import xyz.dev66.jumpropecounter.views.VolumeVisualizerLayout
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


@ExperimentalTime
class CounterFragment(val counterListener: ICounterListener) : Fragment() {

    private val LOG_TAG = CounterFragment::class.java.simpleName

    interface ICounterListener {
        fun onCounterCompleted(count: Int)
    }

    @BindView(R.id.tv_counter)
    lateinit var tvCounter: TextView

    @BindView(R.id.tv_starter)
    lateinit var tvStarter: TextView

    @BindView(R.id.volume_visualizer)
    lateinit var layoutVolumeVisualizer: VolumeVisualizerLayout

    private val rememberTexts: HashSet<CharSequence> = HashSet()

    private val recorder by lazy {
        Recorder()
    }

    private val timerCounter by lazy {
        initTimerCounter()
    }

    private val timerStarter by lazy {
        initTimerStarter()
    }

    private fun say(text: CharSequence) {
        val isChinese = getText(R.string.start_text) == "开始"
        Speaker.getInstance(this.context!!, isChinese).say(text)
    }

    private fun initTimerStarter(): CountDownTimer {
        return object: CountDownTimer(STARTER_MILLIS_IN_FUTURE, STARTER_COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                tvStarter.textSize = 96 * ((1000 - millisUntilFinished % 1000) / 1000.0f + 1.0f)
                tvStarter.text = ((millisUntilFinished / 1000) + 1).toString()

                if (tvStarter.text in rememberTexts) {
                    // Avoid repeating the same texts during one cycle.
                    return
                }

                rememberTexts.add(tvStarter.text)

                this@CounterFragment.say(tvStarter.text)

                if (tvStarter.text == "1") {
                    this@CounterFragment.say(getText(R.string.start_text))
                }
            }

            override fun onFinish() {
                startCounterAfterStarterCompleted()
                rememberTexts.clear()
            }
        }
    }

    private fun initTimerCounter(): CountDownTimer {
        return object: CountDownTimer(COUNTER_MILLIS_IN_FUTURE, COUNTER_COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                val duration = millisUntilFinished.toDuration(TimeUnit.MILLISECONDS)
                tvCounter.text = formatTime(duration)

                val volume = recorder.readVolume()
                layoutVolumeVisualizer.receive(volume, millisUntilFinished)
            }

            override fun onFinish() {
                val lastCount = layoutVolumeVisualizer.receive(0, 0)
                counterListener.onCounterCompleted(lastCount)
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
        layoutVolumeVisualizer.reset()

        tvStarter.visibility = View.INVISIBLE

        tvCounter.text = formatTime(COUNTER_MILLIS_IN_FUTURE.toDuration(TimeUnit.MILLISECONDS))
        tvCounter.visibility = View.VISIBLE

        recorder.start()

        timerCounter.start()
    }

    private fun stopCounter() {
        tvStarter.visibility = View.INVISIBLE

        timerStarter.cancel()

        timerCounter.cancel()

        recorder.stop()

        layoutVolumeVisualizer.reset()
    }

    fun start() {
        rememberTexts.clear()

        startStarter()
    }

    fun stop() {
        stopCounter()

        rememberTexts.clear()
    }
}
