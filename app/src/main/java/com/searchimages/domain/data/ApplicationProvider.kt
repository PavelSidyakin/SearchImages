package com.searchimages.domain.data

import android.content.Context
import com.searchimages.TheApplication

interface ApplicationProvider {
    /**
     * Initializes ApplicationProvider. Must be called before any interaction with ApplicationProvider.
     *
     * @param theApplication application object
     */
    fun init(theApplication: TheApplication)

    /**
     * @returns application context
     */
    val applicationContext: Context
}