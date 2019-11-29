@file:Suppress("DEPRECATION")

package xyz.dev66.jumpropecounter.libs

import android.content.Context
import android.content.res.Configuration
import android.os.Build

class LocaleHelper {
    companion object {
        fun updateResourcesBasedOnConfiguration(context: Context, config: Configuration? = null): Context {
            val notNullConfig = config ?: context.resources.configuration
            return if (Build.VERSION.SDK_INT >= 17) {
                context.createConfigurationContext(notNullConfig)
            } else {
                context.resources.updateConfiguration(notNullConfig, context.resources.displayMetrics)
                context
            }
        }
    }
}