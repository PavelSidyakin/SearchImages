package com.searchimages.data.flickr

import com.searchimages.data.flickr.model.FlickrSearchImagesRequestResult
import com.searchimages.data.flickr.model.FlickrSearchImagesRequestResultCode
import com.searchimages.data.flickr.model.FlickrSearchImagesRequestResultData
import com.searchimages.data.flickr.model.xml.Rsp
import com.searchimages.utils.logs.log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

class FlickrImagesRepositoryImpl

    @Inject
    constructor() : FlickrImagesRepository {

    private val flickrRetrofit: Retrofit by lazy { createRetrofit() }

    override suspend fun searchImages(text: String, pageIndex: Int, pageItemCount: Int): FlickrSearchImagesRequestResult {
        try {
            log { i(TAG, "FlickrImagesRepositoryImpl.searchImages() text=$text, pageIndex=$pageIndex, pageItemCount=$pageItemCount") }

            val rsp: Rsp = createSearchImagesService().searchImages(text, pageIndex, pageItemCount)

            log { i(TAG, "FlickrImagesRepositoryImpl.searchImages() result=$rsp") }

            if (rsp.stat != "ok") {
                return FlickrSearchImagesRequestResult(FlickrSearchImagesRequestResultCode.GENERAL_ERROR, null)
            }

            return FlickrSearchImagesRequestResult(FlickrSearchImagesRequestResultCode.OK, FlickrSearchImagesRequestResultData(rsp.photoList))
        } catch (throwable: Throwable) {
            log { w(TAG, "FlickrImagesRepositoryImpl.searchImages()", throwable) }
            return FlickrSearchImagesRequestResult(FlickrSearchImagesRequestResultCode.GENERAL_ERROR, null)
        }
    }

    private fun createRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor {message ->
            log { i(TAG, message) }
        }
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://www.flickr.com/services/rest/")
            .client(client)
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(Persister(AnnotationStrategy())))
            .build()
    }

    private fun createSearchImagesService(): SearchImagesService {
        return flickrRetrofit.create(SearchImagesService::class.java)
    }

    private interface SearchImagesService {
        @GET("?method=flickr.photos.search&api_key=${API_KEY}")
        suspend fun searchImages(
            @Query("text") text: String,
            @Query("page") pageIndex: Int,
            @Query("per_page") pageItemCount: Int): Rsp
    }

    companion object {
        private const val TAG = "FlickrImagesRepo"
        private const val API_KEY = "6ead3115db4294c1751d80a92758e706"
    }
}