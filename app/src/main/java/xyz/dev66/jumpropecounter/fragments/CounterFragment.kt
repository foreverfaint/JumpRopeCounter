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
import xyz.dev66.jumpropecounter.libs.formatTime
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

const val COUNTER_MILLIS_IN_FUTURE: Long = 60000

const val COUNTER_COUNT_DOWN_INTERVAL: Long = 10

const val STARTER_MILLIS_IN_FUTURE: Long = 3000

const val STARTER_COUNT_DOWN_INTERVAL: Long = 100

@ExperimentalTime
class CounterFragment(val counterListener: ICounterListener) : Fragment() {

    interface ICounterListener {
        fun onCounterCompleted(count: Int)
    }

    @BindView(R.id.tv_counter)
    lateinit var tvCounter: TextView

    @BindView(R.id.tv_starter)
    lateinit var tvStarter: TextView

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
        tvStarter.visibility = View.INVISIBLE

        tvCounter.text = formatTime(COUNTER_MILLIS_IN_FUTURE.toDuration(TimeUnit.MILLISECONDS))
        tvCounter.visibility = View.VISIBLE

        timerCounter.start()
    }

    private fun stopCounter() {
        tvStarter.visibility = View.INVISIBLE

        timerStarter.cancel()

        timerCounter.cancel()
    }

    fun start() {
        startStarter()
    }

    fun stop() {
        stopCounter()
    }
}