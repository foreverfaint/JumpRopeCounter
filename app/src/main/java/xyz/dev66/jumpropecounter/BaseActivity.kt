package xyz.dev66.jumpropecounter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import xyz.dev66.jumpropecounter.libs.LocaleHelper

abstract class BaseActivity : AppCompatActivity(){

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleHelper.updateResourcesBasedOnConfiguration(base!!))
    }
}