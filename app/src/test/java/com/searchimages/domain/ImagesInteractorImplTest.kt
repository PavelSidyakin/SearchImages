package com.searchimages.domain

import com.searchimages.domain.data.ImagesRepository
import com.searchimages.model.SearchImagesResult
import com.searchimages.model.SearchImagesResultCode
import com.searchimages.model.data.SearchImagesRequestImageData
import com.searchimages.model.data.SearchImagesRequestResult
import com.searchimages.model.data.SearchImagesRequestResultCode
import com.searchimages.model.data.SearchImagesRequestResultData
import com.searchimages.utils.NetworkUtils
import com.searchimages.utils.logs.XLog
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@DisplayName("ImagesInteractorImpl tests")
class ImagesInteractorImplTest {

    @Mock
    private lateinit var networkUtils: NetworkUtils
    @Mock
    private lateinit var imagesRepository: ImagesRepository

    @InjectMocks
    private lateinit var imagesInteractor: ImagesInteractorImpl

    @BeforeEach
    fun beforeEachTest() {
        XLog.enableLogging(false)

        MockitoAnnotations.initMocks(this)
    }

    @Test
    @DisplayName("when networkConnectionOn throws an exception, should return GENERAL_ERROR")
    fun searchImagesTest1() {
        runBlocking {
            Mockito.`when`(networkUtils.networkConnectionOn).thenThrow(RuntimeException(""))

            Mockito.`when`(imagesRepository.searchImages(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(SearchImagesRequestResult(SearchImagesRequestResultCode.OK, null))

            val searchImagesResult: SearchImagesResult = imagesInteractor.searchImages(SEARCH_TEXT, PAGE_INDEX, PAGE_SIZE)

            assert(searchImagesResult.resultCode == SearchImagesResultCode.GENERAL_ERROR)
        }
    }

    @Nested
    @DisplayName("When no connection")
    inner class NoInternet {
        @BeforeEach
        fun beforeEachTest() {
            Mockito.`when`(networkUtils.networkConnectionOn).thenReturn(false)
        }

        @Test
        @DisplayName("searchImages() should emit error NO_CONNECTION")
        fun searchImagesTest0() {
            runBlocking {
                Mockito.`when`(imagesRepository.searchImages(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(SearchImagesRequestResult(SearchImagesRequestResultCode.OK, null))

                val searchImagesResult: SearchImagesResult = imagesInteractor.searchImages(SEARCH_TEXT, PAGE_INDEX, PAGE_SIZE)

                assert(searchImagesResult.resultCode == SearchImagesResultCode.NO_NETWORK)
            }
        }

        @AfterEach
        fun afterEach() {
            runBlocking {
                Mockito.verify(imagesRepository, Mockito.never()).searchImages(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())
            }
        }
    }

    @Nested
    @DisplayName("When connection OK")
    inner class HasInternet {
        @BeforeEach
        fun beforeEachTest() {
            Mockito.`when`(networkUtils.networkConnectionOn).thenReturn(true)
        }

        @Test
        @DisplayName("when repo's searchImages() returns GENERAL_ERROR, should return GENERAL_ERROR")
        fun searchImagesTest0() {
            runBlocking {
                Mockito.`when`(imagesRepository.searchImages(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(SearchImagesRequestResult(SearchImagesRequestResultCode.GENERAL_ERROR, null))

                val searchImagesResult: SearchImagesResult = imagesInteractor.searchImages(SEARCH_TEXT, PAGE_INDEX, PAGE_SIZE)

                assert(searchImagesResult.resultCode == SearchImagesResultCode.GENERAL_ERROR)
            }
        }

        @Test
        @DisplayName("when repo's searchImages() throws an exception, should return GENERAL_ERROR")
        fun searchImagesTest1() {
            runBlocking {
                Mockito.`when`(imagesRepository.searchImages(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenThrow(RuntimeException(""))

                val searchImagesResult: SearchImagesResult = imagesInteractor.searchImages(SEARCH_TEXT, PAGE_INDEX, PAGE_SIZE)

                assert(searchImagesResult.resultCode == SearchImagesResultCode.GENERAL_ERROR)
            }
        }

        @Test
        @DisplayName("when repo's searchImages() returns OK, but no data, should return GENERAL_ERROR")
        fun searchImagesTest3() {
            runBlocking {
                Mockito.`when`(imagesRepository.searchImages(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(SearchImagesRequestResult(SearchImagesRequestResultCode.OK, null))

                val searchImagesResult: SearchImagesResult = imagesInteractor.searchImages(SEARCH_TEXT, PAGE_INDEX, PAGE_SIZE)

                assert(searchImagesResult.resultCode == SearchImagesResultCode.GENERAL_ERROR)
            }
        }

        @Test
        @DisplayName("when repo's searchImages() returns OK, and has data, should return OK and match data")
        fun searchImagesTest4() {
            runBlocking {

                val id0 = "000"
                val urlMedium0 = "cnfecnjerncerj"
                val urlLarge0 = "dvrvrbtnuyjiu"

                val id1 = "111"
                val urlMedium1 = "r443rf4grtgfr"
                val urlLarge1 = "332r4g67h786h"


                Mockito.`when`(imagesRepository.searchImages(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(SearchImagesRequestResult(SearchImagesRequestResultCode.OK, SearchImagesRequestResultData(
                        listOf(
                            SearchImagesRequestImageData(id0, urlMedium0, urlLarge0),
                            SearchImagesRequestImageData(id1, urlMedium1, urlLarge1))
                    )))

                val searchImagesResult: SearchImagesResult = imagesInteractor.searchImages(SEARCH_TEXT, PAGE_INDEX, PAGE_SIZE)

                assert(searchImagesResult.resultCode == SearchImagesResultCode.OK)

                assert(searchImagesResult.searchImagesResultData!!.imagesData[0].id == id0)
                assert(searchImagesResult.searchImagesResultData!!.imagesData[0].urlMedium == urlMedium0)
                assert(searchImagesResult.searchImagesResultData!!.imagesData[0].urlLarge == urlLarge0)

                assert(searchImagesResult.searchImagesResultData!!.imagesData[1].id == id1)
                assert(searchImagesResult.searchImagesResultData!!.imagesData[1].urlMedium == urlMedium1)
                assert(searchImagesResult.searchImagesResultData!!.imagesData[1].urlLarge == urlLarge1)

            }
        }

    }

    private companion object {
        private const val SEARCH_TEXT = "cat"
        private const val PAGE_INDEX = 111
        private const val PAGE_SIZE = 222
    }
}