package com.searchimages.data

import android.content.Context
import com.searchimages.TheApplication
import com.searchimages.domain.data.ApplicationProvider
import javax.inject.Inject

class ApplicationProviderImpl
    @Inject
    constructor() : ApplicationProvider {

    private lateinit var theApplication: TheApplication

    override fun init(theApplication: TheApplication) {
        this.theApplication = theApplication
    }

    override val applicationContext: Context
        get() = theApplication.applicationContext


}