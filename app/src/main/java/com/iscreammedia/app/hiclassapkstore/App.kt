package com.iscreammedia.app.hiclassapkstore

import android.app.Application
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(AppModules.modules)
        }

        Stetho.initializeWithDefaults(this)
    }
}