package com.searchimages.data.flickr.model

import com.searchimages.data.flickr.model.xml.Photo

data class FlickrSearchImagesRequestResultData (
    val photoList: List<Photo>?
)