package com.searchimages.presentation.presenter.search

import android.arch.paging.DataSource
import com.searchimages.domain.ImagesInteractor
import com.searchimages.model.SearchImagesImageData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlin.coroutines.CoroutineContext

class SearchImagesDataSourceFactory @ExperimentalCoroutinesApi constructor(
    private val text: String,
    private val imagesInteractor: ImagesInteractor,
    private val searchImagesPresenter: SearchImagesPresenter,
    private val coroutineContext: CoroutineContext,
    private val retryChannel: BroadcastChannel<Any>

) : DataSource.Factory<Int, SearchImagesImageData>()  {

    override fun create(): DataSource<Int, SearchImagesImageData> {
        return SearchImagesDataSource(
            text,
            imagesInteractor,
            searchImagesPresenter,
            coroutineContext,
            retryChannel)
    }
}