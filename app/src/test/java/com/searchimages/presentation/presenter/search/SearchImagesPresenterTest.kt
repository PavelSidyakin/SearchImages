package com.searchimages.presentation.presenter.search

import android.arch.paging.PagedList
import com.searchimages.domain.ImagesInteractor
import com.searchimages.model.SearchImagesImageData
import com.searchimages.model.SearchImagesResult
import com.searchimages.model.SearchImagesResultCode
import com.searchimages.model.SearchImagesResultData
import com.searchimages.presentation.view.search.SearchImagesView
import com.searchimages.utils.logs.XLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatcher
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import kotlin.math.min

@DisplayName("SearchImagesPresenter tests")
class SearchImagesPresenterTest {

    @Mock
    private lateinit var imagesInteractor: ImagesInteractor
    @Mock
    private lateinit var searchImagesView: SearchImagesView

    private lateinit var searchImagesPresenter: SearchImagesPresenter

    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Unconfined

    @BeforeEach
    fun beforeEach() {
        XLog.enableLogging(false)
        MockitoAnnotations.initMocks(this)
        setupPresenter()
        searchImagesPresenter.performSearchTimeoutMillis = 0
    }

    @Nested
    @DisplayName("When no network error, should show network error message")
    inner class NoNetwork {
        @BeforeEach
        fun beforeEach() {
            runBlocking(coroutineDispatcher) {
                Mockito.`when`(imagesInteractor.searchImages(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(SearchImagesResult(SearchImagesResultCode.NO_NETWORK, null))
            }
        }

        @Test
        @DisplayName("on text submitted")
        fun onSearchTextSubmitted() {

            // action
            searchImagesPresenter.onSearchTextSubmitted(LAST_ENTERED_TEXT)
        }

        @Test
        @DisplayName("on text entered")
        fun onSearchTextChanged() {

            // action
            searchImagesPresenter.onSearchTextChanged(LAST_ENTERED_TEXT)
        }

        @AfterEach
        fun afterEach() {
            // verify
            runBlocking(coroutineDispatcher) {
                Mockito.verify(imagesInteractor, Mockito.times(1)).searchImages(LAST_ENTERED_TEXT, 1, INITIAL_PAGE_SIZE)
            }
            Mockito.verify(searchImagesView, Mockito.never()).showGeneralError(true)
            Mockito.verify(searchImagesView).showNoNetworkError(true)
        }

    }

    @Nested
    @DisplayName("When general error, should show general error message")
    inner class GeneralError {
        @BeforeEach
        fun beforeEach() {
            runBlocking(coroutineDispatcher) {
                Mockito.`when`(imagesInteractor.searchImages(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(SearchImagesResult(SearchImagesResultCode.GENERAL_ERROR, null))
            }
        }

        @Test
        @DisplayName("on text submitted")
        fun onSearchTextSubmitted() {

            // action
            searchImagesPresenter.onSearchTextSubmitted(LAST_ENTERED_TEXT)
        }

        @Test
        @DisplayName("on text entered")
        fun onSearchTextChanged() {

            // action
            searchImagesPresenter.onSearchTextChanged(LAST_ENTERED_TEXT)
        }

        @AfterEach
        fun afterEach() {
            // verify
            runBlocking(coroutineDispatcher) {
                Mockito.verify(imagesInteractor, Mockito.times(1)).searchImages(LAST_ENTERED_TEXT, 1, INITIAL_PAGE_SIZE)
            }
            Mockito.verify(searchImagesView).showGeneralError(true)
            Mockito.verify(searchImagesView, Mockito.never()).showNoNetworkError(true)
        }

    }

    @Nested
    @DisplayName("When successful searchImages() result")
    inner class SuccessfulResult {
        @BeforeEach
        fun beforeEach() {
            runBlocking(coroutineDispatcher) {
                Mockito.`when`(imagesInteractor.searchImages(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(SearchImagesResult(SearchImagesResultCode.OK, SearchImagesResultData(listOf(SearchImagesImageData("id", "u1", "u2")))))
            }
        }

        @Nested
        @DisplayName("Should be only one request with the last entered text")
        inner class ShouldBeOnlyOneRequestTests {
            @Test
            @DisplayName("when typing text and then submit")
            fun shouldBeOnlyOneRequest1() {

                // action
                searchImagesPresenter.onSearchTextChanged("aaaaaa")
                searchImagesPresenter.onSearchTextSubmitted(LAST_ENTERED_TEXT)
            }

            @Test
            @DisplayName("when typing one text, then another")
            fun shouldBeOnlyOneRequest2() {

                // action
                searchImagesPresenter.onSearchTextChanged("aaaa")
                searchImagesPresenter.onSearchTextChanged("bbbb")
                searchImagesPresenter.onSearchTextChanged("cccc")
                searchImagesPresenter.onSearchTextChanged(LAST_ENTERED_TEXT)
            }

            @Test
            @DisplayName("when submit text")
            fun shouldBeOnlyOneRequest3() {

                // action
                searchImagesPresenter.onSearchTextSubmitted(LAST_ENTERED_TEXT)
            }

            @AfterEach
            fun afterEachTest() {

                // verify
                Mockito.verify(searchImagesView, Mockito.never()).showGeneralError(true)
                val inOrder: InOrder = Mockito.inOrder(searchImagesView)
                inOrder.verify(searchImagesView).showProgress(true)
                inOrder.verify(searchImagesView).showProgress(false)

                runBlocking(coroutineDispatcher) {
                    Mockito.verify(imagesInteractor, Mockito.times(1)).searchImages(LAST_ENTERED_TEXT, 1, INITIAL_PAGE_SIZE)
                }
            }
        }
    }

    @Nested
    @DisplayName("When successful searchImages() result (multiple requests)")
    inner class SuccessfulResultMultipleRequests {
        private val imagesStream: MutableList<String> = listOf<String>().toMutableList()
        private val pageSize = 5
        private val initialPageSizeFactor = 4
        private val imageCount = 113

        init {
            for (i in 0..imageCount) {
                imagesStream.add("$i")
            }
        }

        @BeforeEach
        fun beforeEach() {
            searchImagesPresenter.pageSize = pageSize
            searchImagesPresenter.initialPageSizeFactor = initialPageSizeFactor

            runBlocking(coroutineDispatcher) {
                Mockito.`when`(imagesInteractor.searchImages(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenAnswer { invocation ->
                    val pageIndex: Int = invocation.getArgument(1)
                    val pageItemCount: Int = invocation.getArgument(2)

                    //println("searchImages() pageIndex=$pageIndex pageItemCount=$pageItemCount")

                    val imagesList: MutableList<SearchImagesImageData> = listOf<SearchImagesImageData>().toMutableList()

                    for (i in (pageIndex - 1) * pageItemCount until min(pageItemCount * pageIndex, imagesStream.size)) {
                        imagesList.add(SearchImagesImageData(imagesStream[i], "", ""))
                    }

                    //println("searchImages() imagesList.size=${imagesList.size} imagesList.last=${imagesList.last()}")

                    SearchImagesResult(SearchImagesResultCode.OK, SearchImagesResultData(imagesList))
                }
            }
        }

        @Test
        @DisplayName("image stream should correspond to source")
        fun imageStreamShouldCorrespondToSource() {

            // action
            searchImagesPresenter.onSearchTextSubmitted("cat")


            Mockito.verify(searchImagesView).updateDisplayData(argThatK(ArgumentMatcher { list ->

                var areListsIdentical = false

                for (image in pageSize * initialPageSizeFactor .. imageCount) {

                    //println("updateDisplayData() list to display: size=${list.size} last=${list.last().id}")

                    val imageListToCheck = listOf<String>().toMutableList()

                    for (i in 0 until list.size) {
                        imageListToCheck.add(imagesStream[i])
                    }

                    areListsIdentical = areListsIdentical(imageListToCheck, list)

                    if (!areListsIdentical) {
                        println("lists are different: $imageListToCheck")
                        println("expected list: $imageListToCheck")
                        println("  actual list: ${list.map { it.id }}")
                        break
                    }

                    list.loadAround(image)
                }

                return@ArgumentMatcher areListsIdentical
            }))

        }

        private fun <T> argThatK(matcher: ArgumentMatcher<T>): T {
            Mockito.argThat<T>(matcher)
            return uninitialized()
        }

        private fun <T> uninitialized(): T = null as T

        private fun areListsIdentical(imageList: List<String>, pagedList: PagedList<SearchImagesImageData> ): Boolean {
            if (imageList.size != pagedList.size) {
                return false
            }

            for (i in 0 until imageList.size) {
                if (imageList[i] != pagedList[i]?.id) {
                    return false
                }
            }

            return true
        }

    }

    @Nested
    @DisplayName("When failed and then successful searchImages() result, should show error")
    inner class FailedSearchRequest {
        @BeforeEach
        fun beforeEach() {
            runBlocking(coroutineDispatcher) {
                Mockito.`when`(imagesInteractor.searchImages(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(SearchImagesResult(SearchImagesResultCode.GENERAL_ERROR, null))
                    .thenReturn(SearchImagesResult(SearchImagesResultCode.OK, SearchImagesResultData(listOf(SearchImagesImageData("id2", "u3", "u4")))))
            }
        }

        @Test
        @DisplayName("searchImages() shouldn't be called one more time")
        fun showError() {

            // action
            searchImagesPresenter.onSearchTextSubmitted(LAST_ENTERED_TEXT)

            // verify
            runBlocking(coroutineDispatcher) {
                Mockito.verify(imagesInteractor, Mockito.times(1)).searchImages(LAST_ENTERED_TEXT, 1, INITIAL_PAGE_SIZE)
            }
        }

        @Test
        @DisplayName("click on error message should retry request")
        fun clickOnError() {

            // action
            searchImagesPresenter.onSearchTextSubmitted(LAST_ENTERED_TEXT)
            searchImagesPresenter.onErrorClicked()


            // verify
            runBlocking(coroutineDispatcher) {
                Mockito.verify(imagesInteractor, Mockito.times(2)).searchImages(LAST_ENTERED_TEXT, 1, INITIAL_PAGE_SIZE)
            }
        }

        @AfterEach
        fun afterEachTest() {
            Mockito.verify(searchImagesView).showGeneralError(true)
            Mockito.verify(searchImagesView, Mockito.never()).showNoNetworkError(true)
            val inOrder: InOrder = Mockito.inOrder(searchImagesView)
            inOrder.verify(searchImagesView).showProgress(true)
            inOrder.verify(searchImagesView).showProgress(false)
        }
    }

    private fun setupPresenter() {
        searchImagesPresenter = SearchImagesPresenter(imagesInteractor, coroutineDispatcher, coroutineDispatcher)
        searchImagesPresenter.attachView(searchImagesView)
    }

    companion object {
        private const val LAST_ENTERED_TEXT = "LAST ENTERED TEXT aaaaaa"

        private const val INITIAL_PAGE_SIZE = SearchImagesPresenter.DEFAULT_INITIAL_PAGE_SIZE_FACTOR * SearchImagesPresenter.DEFAULT_PAGE_SIZE
    }
}