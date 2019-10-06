package com.searchimages.domain

import com.searchimages.domain.data.ImagesRepository
import com.searchimages.model.SearchImagesImageData
import com.searchimages.model.SearchImagesResult
import com.searchimages.model.SearchImagesResultCode
import com.searchimages.model.SearchImagesResultData
import com.searchimages.model.data.SearchImagesRequestResult
import com.searchimages.model.data.SearchImagesRequestResultCode
import com.searchimages.model.data.SearchImagesRequestResultData
import com.searchimages.utils.NetworkUtils
import com.searchimages.utils.logs.log
import javax.inject.Inject

class ImagesInteractorImpl
    @Inject
    constructor(
        private val networkUtils: NetworkUtils,
        private val imagesRepository: ImagesRepository
    ) : ImagesInteractor {
    override suspend fun searchImages(text: String, pageIndex: Int, pageItemCount: Int): SearchImagesResult {

        try {
            log { i(TAG, "ImagesInteractor.searchImages() text=$text, pageIndex=$pageIndex, pageItemCount=$pageItemCount") }

            if (!networkUtils.networkConnectionOn) {
                return SearchImagesResult(SearchImagesResultCode.NO_NETWORK, null)
            }

            val searchImagesRequestResult: SearchImagesRequestResult = imagesRepository.searchImages(text, pageIndex, pageItemCount)

            log { i(TAG, "ImagesInteractor.searchImages() searchImagesRequestResult=$searchImagesRequestResult") }

            if (searchImagesRequestResult.resultCode != SearchImagesRequestResultCode.OK ||
                searchImagesRequestResult.searchImagesRequestResultData == null) {
                return SearchImagesResult(SearchImagesResultCode.GENERAL_ERROR, null)
            }

            return SearchImagesResult(SearchImagesResultCode.OK, convertSearchImagesRequestResultData2SearchImagesResultData(searchImagesRequestResult.searchImagesRequestResultData))

        } catch (throwable: Throwable) {
            return SearchImagesResult(SearchImagesResultCode.GENERAL_ERROR, null)
        }
    }

    private fun convertSearchImagesRequestResultData2SearchImagesResultData(requestResultData: SearchImagesRequestResultData?): SearchImagesResultData? {
        return requestResultData?.let { SearchImagesResultData(requestResultData.imageList.map {searchImagesRequestImageData ->
                SearchImagesImageData(searchImagesRequestImageData.id, searchImagesRequestImageData.urlMedium, searchImagesRequestImageData.urlLarge)
            })
        }

    }

    companion object {
        private const val TAG = "ImagesInteractor"
    }

}