package com.searchimages.presentation.presenter.search

import android.arch.paging.PagedList
import android.support.annotation.VisibleForTesting
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.searchimages.domain.ImagesInteractor
import com.searchimages.model.SearchImagesImageData
import com.searchimages.model.SearchImagesResultCode
import com.searchimages.presentation.view.search.SearchImagesView
import com.searchimages.utils.logs.log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@InjectViewState
class SearchImagesPresenter(
    val imagesInteractor: ImagesInteractor,
    val coroutineMainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    val coroutineIoDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MvpPresenter<SearchImagesView>(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext get() = job +  coroutineMainDispatcher
    private var requestJob: Job? = null

    private val pageListConfig = PagedList.Config.Builder()
        .setPageSize(PAGE_SIZE)
        .setInitialLoadSizeHint(INITIAL_PAGE_SIZE)
        .setEnablePlaceholders(false)
        .build()

    private val textChannel = BroadcastChannel<String>(1)

    private val retryChannel = BroadcastChannel<Any>(1)

    private var requestedText = ""

    @VisibleForTesting
    var performSearchTimeoutMillis: Long = PERFORM_SEARCH_DEFAULT_TIMEOUT_MILLIS


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.showProgress(false)

        GlobalScope.launch (coroutineContext) {

            textChannel.asFlow()
                .collect { text ->

                    requestJob?.cancel("New text came")

                    requestJob = GlobalScope.launch(coroutineContext) {
                        viewState.updateDisplayData(buildDataSource(text))
                    }
                }
        }
    }

    private fun buildDataSource(text: String): PagedList<SearchImagesImageData> {


        val factory = SearchImagesDataSourceFactory(text, imagesInteractor, this@SearchImagesPresenter, coroutineContext, retryChannel)

        return PagedList.Builder(factory.create(), pageListConfig)
            .setNotifyExecutor { GlobalScope.launch(coroutineMainDispatcher) { it.run() } }
            .setFetchExecutor { GlobalScope.launch(coroutineIoDispatcher) { it.run() } }
            .build()
    }

    // Data source callbacks

    fun onRequestStarted() {
        viewState.showProgress(true)
    }

    fun onResult(searchImagesResultCode: SearchImagesResultCode) {
        viewState.showProgress(false)

        when(searchImagesResultCode) {
            SearchImagesResultCode.OK -> hideAllErrors()
            SearchImagesResultCode.NO_NETWORK -> viewState.showNoNetworkError(true)
            SearchImagesResultCode.GENERAL_ERROR -> viewState.showGeneralError(true)
        }
    }
    // ^ Data source callbacks

    fun onImageIsClicked(image: SearchImagesImageData) {
        log { i(TAG, "Image is clicked. image=$image") }

        // TODO: handle an image click
    }

    fun onSearchTextChanged(searchText: String) {
        log { i(TAG, "Entered search string: $searchText") }

        if (searchText.isEmpty()) {
            viewState.clearList()
            return
        }

        if (requestedText != searchText) {
            requestedText = searchText

            GlobalScope.launch(coroutineContext) {
                delay(performSearchTimeoutMillis)
                if (requestedText == searchText) {
                    textChannel.send(searchText)
                }
            }
        }

    }

    fun onSearchTextSubmitted(searchText: String) {
        log { i(TAG, "Entered search string: $searchText") }

        if (searchText.isEmpty()) {
            viewState.clearList()
            return
        }

        if (!searchText.isEmpty() && requestedText != searchText) {
            requestedText = searchText

            GlobalScope.launch(coroutineContext) {
                if (requestedText == searchText) {
                    textChannel.send(searchText)
                }
            }
        }
    }

    fun onErrorClicked() {
        GlobalScope.launch(coroutineContext) {
            retryChannel.send(Any())
        }
    }

    private fun hideAllErrors() {
        viewState.showGeneralError(false)
        viewState.showNoNetworkError(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel("SearchImagesPresenter.onDestroy()")
    }

    companion object {
        private const val TAG = "SearchImagesPresenter"
        private const val PERFORM_SEARCH_DEFAULT_TIMEOUT_MILLIS = 500L

        @VisibleForTesting
        const val PAGE_SIZE = 50
        @VisibleForTesting
        const val INITIAL_PAGE_SIZE_FACTOR = 3
        @VisibleForTesting
        const val INITIAL_PAGE_SIZE = PAGE_SIZE * INITIAL_PAGE_SIZE_FACTOR


    }
}
