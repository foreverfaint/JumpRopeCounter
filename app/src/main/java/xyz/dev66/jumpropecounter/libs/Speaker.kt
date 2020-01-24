package xyz.dev66.jumpropecounter.libs

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class Speaker private constructor(private val context: Context, private val isChinese: Boolean) {

    private val LOG_TAG = Speaker::class.java.simpleName

    private var textToSpeech: TextToSpeech? = null

    fun initialize() {
        if (textToSpeech != null) {
            return
        }

        Log.v(LOG_TAG, "TextToSpeech New Started in Thread ${Thread.currentThread().id}")

        textToSpeech = TextToSpeech(this.context, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech!!.setPitch(1.0f)
                textToSpeech!!.setSpeechRate(1.0f)
                if (isChinese) {
                    textToSpeech!!.language = Locale.SIMPLIFIED_CHINESE
                } else {
                    textToSpeech!!.language = Locale.ENGLISH
                }
            }

            Log.v(LOG_TAG, "TextToSpeech OnInitListener in [${Thread.currentThread().id}]")
        })

        Log.v(LOG_TAG, "TextToSpeech New Completed in [${Thread.currentThread().id}]")
    }

    fun destroy() {
        if (textToSpeech == null) {
            return
        }

        textToSpeech!!.stop()
        textToSpeech!!.shutdown()
        textToSpeech = null
    }

    fun say(text: CharSequence) {
        val ret = textToSpeech!!.speak(text, TextToSpeech.QUEUE_ADD, null, null)
        Log.v(LOG_TAG, "TextToSpeech speak returns $ret in [${Thread.currentThread().id}]")
    }

    companion object {
        @Volatile
        var instance: Speaker? = null

        fun getInstance(context: Context, isChinese: Boolean): Speaker {
            if (instance == null) {
                synchronized(Speaker::class) {
                    if (instance == null) {
                        instance = Speaker(context, isChinese)
                    }
                }
            }
            return instance!!
        }
    }
}