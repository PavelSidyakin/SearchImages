package com.searchimages.data.flickr

import com.searchimages.data.flickr.model.FlickrSearchImagesRequestResult

interface FlickrImagesRepository {

    /**
     * Performs search request with the given search text and paging parameters
     *
     * @param text Search string
     * @param pageIndex Page index (1-based)
     * @param pageItemCount Item count on each page
     *
     * @exception Nothing
     */
    suspend fun searchImages(text: String, pageIndex: Int, pageItemCount: Int): FlickrSearchImagesRequestResult

}