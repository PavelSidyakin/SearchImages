package com.searchimages.di

import com.searchimages.data.ApplicationProviderImpl
import com.searchimages.data.ImagesRepositoryImpl
import com.searchimages.data.flickr.FlickrImagesRepository
import com.searchimages.data.flickr.FlickrImagesRepositoryImpl
import com.searchimages.domain.ImagesInteractor
import com.searchimages.domain.ImagesInteractorImpl
import com.searchimages.domain.data.ApplicationProvider
import com.searchimages.domain.data.ImagesRepository
import com.searchimages.utils.NetworkUtils
import com.searchimages.utils.NetworkUtilsImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun provideApplicationProvider(applicationProvider: ApplicationProviderImpl) : ApplicationProvider

    @Singleton
    @Binds
    abstract fun provideNetworkUtils(networkUtils: NetworkUtilsImpl) : NetworkUtils

    @Singleton
    @Binds
    abstract fun provideImagesRepository(imagesRepository: ImagesRepositoryImpl) : ImagesRepository

    @Singleton
    @Binds
    abstract fun provideFlickrImagesRepository(flickrImagesRepository: FlickrImagesRepositoryImpl) : FlickrImagesRepository

    @Singleton
    @Binds
    abstract fun provideImagesInteractor(imagesInteractor: ImagesInteractorImpl) : ImagesInteractor







}