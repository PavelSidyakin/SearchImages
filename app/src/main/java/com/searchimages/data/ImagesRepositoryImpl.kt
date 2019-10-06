package com.searchimages.data

import com.searchimages.data.flickr.FlickrImagesRepository
import com.searchimages.domain.data.ImagesRepository
import com.searchimages.data.flickr.model.FlickrSearchImagesRequestResult
import com.searchimages.data.flickr.model.FlickrSearchImagesRequestResultCode
import com.searchimages.data.flickr.model.FlickrSearchImagesRequestResultData
import com.searchimages.data.flickr.model.xml.Photo
import com.searchimages.model.data.SearchImagesRequestImageData
import com.searchimages.model.data.SearchImagesRequestResult
import com.searchimages.model.data.SearchImagesRequestResultCode
import com.searchimages.model.data.SearchImagesRequestResultData
import com.searchimages.utils.logs.log
import javax.inject.Inject

class ImagesRepositoryImpl
    @Inject
    constructor(
        private val flickrImagesRepository: FlickrImagesRepository
    ) : ImagesRepository {


    override suspend fun searchImages(text: String, pageIndex: Int, pageItemCount: Int): SearchImagesRequestResult {
        try {

            log { i(TAG, "ImagesRepositoryImpl.searchImages() text=$text, pageIndex=$pageIndex, pageItemCount=$pageItemCount") }

            val flickrSearchImagesRequestResult: FlickrSearchImagesRequestResult = flickrImagesRepository.searchImages(text, pageIndex, pageItemCount)

            log { i(TAG, "ImagesRepositoryImpl.searchImages() flickrSearchImagesRequestResult=$flickrSearchImagesRequestResult") }

            if (flickrSearchImagesRequestResult.resultCode != FlickrSearchImagesRequestResultCode.OK) {
                return SearchImagesRequestResult(SearchImagesRequestResultCode.GENERAL_ERROR, null)
            }

            if (flickrSearchImagesRequestResult.searchImagesRequestResultData == null) {
                return SearchImagesRequestResult(SearchImagesRequestResultCode.GENERAL_ERROR, null)
            }

            return SearchImagesRequestResult(
                SearchImagesRequestResultCode.OK,
                convertFlickrSearchImagesRequestResultData2SearchImagesRequestResultData(flickrSearchImagesRequestResult.searchImagesRequestResultData)
            )

        } catch (throwable: Throwable) {
            log { w(TAG, "ImagesRepositoryImpl.searchImages()", throwable) }
            return SearchImagesRequestResult(SearchImagesRequestResultCode.GENERAL_ERROR, null)
        }
    }

    private fun convertFlickrSearchImagesRequestResultData2SearchImagesRequestResultData(flickrData: FlickrSearchImagesRequestResultData): SearchImagesRequestResultData? {
        return flickrData.photoList?.let {
            SearchImagesRequestResultData(flickrData.photoList.map { photo: Photo -> convertFlickrPhotoToImageData(photo) })
        }
    }

    private fun convertFlickrPhotoToImageData(photo: Photo): SearchImagesRequestImageData {
        val urlStart = "https://farm${photo.farmId}.staticflickr.com/${photo.serverId}/${photo.id}_${photo.secret}_"

        return SearchImagesRequestImageData(
            id = photo.id,
            urlMedium = urlStart + "q.png",
            urlLarge = urlStart + "b.png"
        )
    }

    companion object {
        private const val TAG = "ImagesRepository"
    }

}