@file:Suppress("DEPRECATION")

package xyz.dev66.jumpropecounter

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import xyz.dev66.jumpropecounter.libs.LocaleHelper

open class JumpRopeCounterApplication : Application() {

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleHelper.updateResourcesBasedOnConfiguration(base!!))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleHelper.updateResourcesBasedOnConfiguration(this, newConfig)
        Log.d("JumpRopeCounterApplication", "onConfigurationChanged: " + newConfig.locale.getLanguage())
    }
}