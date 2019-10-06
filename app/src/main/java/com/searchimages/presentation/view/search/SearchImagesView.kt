package com.searchimages.presentation.view.search

import android.arch.paging.PagedList
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.searchimages.model.SearchImagesImageData

interface SearchImagesView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun updateDisplayData(data: PagedList<SearchImagesImageData>)

    @StateStrategyType(SkipStrategy::class)
    fun clearList()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showGeneralError(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showNoNetworkError(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showProgress(show: Boolean)
}