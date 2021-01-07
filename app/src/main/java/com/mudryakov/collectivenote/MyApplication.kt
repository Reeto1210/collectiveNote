package com.mudryakov.collectivenote

import android.app.Application
import com.mudryakov.collectivenote.di.DaggerAppComponent
import com.mudryakov.collectivenote.utility.AppPreference


class MyApplication : Application() {

    companion object {
        lateinit var thisApp: Application
        val appComponent by lazy { DaggerAppComponent.factory().create(thisApp) }
         }

    override fun onCreate() {
        super.onCreate()
        thisApp = this
        AppPreference.getPreference(this)
               }

}