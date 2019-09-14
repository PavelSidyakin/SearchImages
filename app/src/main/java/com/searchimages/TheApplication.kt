package com.searchimages

import android.app.Application
import com.searchimages.di.AppComponent
import com.searchimages.di.DaggerAppComponent
import com.searchimages.domain.data.ApplicationProvider
import javax.inject.Inject

class TheApplication : Application() {
    @Inject
    lateinit var applicationProvider: ApplicationProvider

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .build()

        appComponent.inject(this)

        applicationProvider.init(this)

    }

    companion object {
        private const val TAG = "TheApplication"

        private lateinit var appComponent: AppComponent

        fun getAppComponent(): AppComponent {
            return appComponent
        }
    }
}