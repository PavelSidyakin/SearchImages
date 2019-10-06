package com.searchimages.di

import com.searchimages.TheApplication
import com.searchimages.presentation.view.MainActivity
import com.searchimages.presentation.view.search.SearchImagesViewFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(theApplication: TheApplication)
    fun inject(mainActivity: MainActivity)
    fun inject(searchImagesViewFragment: SearchImagesViewFragment)

    interface Builder {
        fun build(): AppComponent
    }
}