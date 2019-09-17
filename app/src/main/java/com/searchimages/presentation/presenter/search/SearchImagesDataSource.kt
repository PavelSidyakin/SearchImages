package com.searchimages.presentation.presenter.search

import android.arch.paging.PageKeyedDataSource
import com.searchimages.domain.ImagesInteractor
import com.searchimages.model.SearchImagesImageData
import com.searchimages.model.SearchImagesResult
import com.searchimages.model.SearchImagesResultCode
import com.searchimages.utils.logs.log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SearchImagesDataSource(
        private val text: String,
        private val imagesInteractor: ImagesInteractor,
        private val searchImagesPresenter: SearchImagesPresenter,
        private val coroutineContext: CoroutineContext,
        private val retryChannel: BroadcastChannel<Any>

) : PageKeyedDataSource<Int, SearchImagesImageData>() {

    init {
        GlobalScope.launch(coroutineContext) {
            retryChannel.asFlow()
                .collect {
                    retry()
                }
        }
    }

    var retryRunnable: Runnable? = null

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, SearchImagesImageData>) {
        GlobalScope.launch (coroutineContext) {
            try {

                searchImagesPresenter.onRequestStarted()

                val searchImagesResult: SearchImagesResult = imagesInteractor.searchImages(text, 1, params.requestedLoadSize)

                log { i(TAG, "SearchImagesDataSource.loadInitial() searchImagesResult=$searchImagesResult") }

                if (searchImagesResult.resultCode == SearchImagesResultCode.OK) {
                    searchImagesResult.searchImagesResultData?.let { data ->
                        callback.onResult(data.imagesData, null, searchImagesPresenter.initialPageSizeFactor + 1)
                    }
                } else {
                    retryRunnable = Runnable { loadInitial(params, callback) }
                }
                searchImagesPresenter.onResult(searchImagesResult.resultCode)

            }catch (throwable: Throwable) {
                log { w(TAG, "SearchImagesDataSource.loadInitial()", throwable) }
                retryRunnable = Runnable { loadInitial(params, callback) }
                searchImagesPresenter.onResult(SearchImagesResultCode.GENERAL_ERROR)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, SearchImagesImageData>) {
        GlobalScope.launch (coroutineContext) {
            try {

                searchImagesPresenter.onRequestStarted()

                val searchImagesResult: SearchImagesResult = imagesInteractor.searchImages(text, params.key, params.requestedLoadSize)
                log { i(TAG, "SearchImagesDataSource.loadAfter() searchImagesResult=$searchImagesResult") }

                if (searchImagesResult.resultCode == SearchImagesResultCode.OK) {
                    searchImagesResult.searchImagesResultData?.let { data ->
                        callback.onResult(data.imagesData, params.key + 1)
                    }
                } else {
                    retryRunnable = Runnable { loadAfter(params, callback) }
                }
                searchImagesPresenter.onResult(searchImagesResult.resultCode)

            }catch (throwable: Throwable) {
                log { w(TAG, "SearchImagesDataSource.loadAfter()", throwable) }
                retryRunnable = Runnable { loadAfter(params, callback) }
                searchImagesPresenter.onResult(SearchImagesResultCode.GENERAL_ERROR)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, SearchImagesImageData>) {
    }


    fun retry() {
        retryRunnable?.run()
    }

    companion object {
        const val TAG = "SearchImagesDataSource"
    }
}