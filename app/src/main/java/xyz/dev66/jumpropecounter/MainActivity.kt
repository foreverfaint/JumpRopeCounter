package xyz.dev66.jumpropecounter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import xyz.dev66.jumpropecounter.fragments.CounterFragment
import xyz.dev66.jumpropecounter.fragments.RecordItemListFragment
import xyz.dev66.jumpropecounter.models.RecordItem

import kotlin.time.ExperimentalTime

const val REQUEST_RECORD_AUDIO = 13

@ExperimentalTime
class MainActivity : BaseActivity(), CounterFragment.ICounterListener {
    @BindView(R.id.fab_recording)
    lateinit var fabRecording: FloatingActionButton

    private val fragmentRecordItemList = RecordItemListFragment()

    private val fragmentCounter = CounterFragment(this)

    @OnClick(R.id.fab_recording)
    fun onClick(view: View) {
        when (view.tooltipText) {
            this@MainActivity.resources.getString(R.string.start_text) -> start()
            this@MainActivity.resources.getString(R.string.stop_text) -> stop()
        }
    }

    override fun onCounterCompleted(count: Int) {
        RecordItem.add(this.application.baseContext, count)
        stop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, fragmentRecordItemList)
            add(R.id.fragment_container, fragmentCounter)
            hide(fragmentCounter)
            show(fragmentRecordItemList)
        }.commit()
    }

    override fun onStop() {
        super.onStop()
        stop()
    }

    private fun start() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
            PackageManager.PERMISSION_GRANTED) {
            requestMicrophonePermission()
            return
        }

        if (fragmentCounter.isVisible) {
            return
        }

        supportFragmentManager.beginTransaction().apply {
            hide(fragmentRecordItemList)
            show(fragmentCounter)
        }.commit()

        fragmentCounter.start()
        fabRecording.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_stop_white_24dp, null))
        fabRecording.tooltipText = resources.getString(R.string.stop_text)
    }

    private fun stop() {
        if (!fragmentCounter.isVisible) {
            return
        }

        supportFragmentManager.beginTransaction().apply {
            hide(fragmentCounter)
            show(fragmentRecordItemList)
        }.commit()

        fragmentCounter.stop()
        fabRecording.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_play_arrow_white_24dp, null))
        fabRecording.tooltipText = resources.getString(R.string.start_text)
    }

    private fun requestMicrophonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            Snackbar.make(
                this.fragmentRecordItemList.view!!,
                resources.getString(R.string.request_microphone_access),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(resources.getString(R.string.ok_text)) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO)
            }.show()
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO)
        }
    }
}
