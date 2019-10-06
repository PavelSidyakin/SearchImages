package com.searchimages.presentation.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.searchimages.R
import com.searchimages.TheApplication
import com.searchimages.data.ImagesRepositoryImpl
import com.searchimages.presentation.view.search.SearchImagesViewFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var imagesRepositoryImpl: ImagesRepositoryImpl


    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        TheApplication.getAppComponent().inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.main_activity_container, SearchImagesViewFragment())

        fragmentTransaction.commit()
        fragmentManager.executePendingTransactions()

    }

}
