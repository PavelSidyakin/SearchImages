package com.searchimages.presentation.view.search

import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.searchimages.R
import com.searchimages.TheApplication
import com.searchimages.domain.ImagesInteractor
import com.searchimages.domain.data.ApplicationProvider
import com.searchimages.model.SearchImagesImageData
import com.searchimages.presentation.presenter.search.SearchImagesPresenter
import com.searchimages.presentation.view.RecyclerViewOnItemClickListener
import com.searchimages.presentation.view.search.recycler.SearchImagesAdapter
import kotlinx.android.synthetic.main.layout_images_search_view.pb_images_search
import kotlinx.android.synthetic.main.layout_images_search_view.rv_images_search_list
import kotlinx.android.synthetic.main.layout_images_search_view.sv_images_search_view
import kotlinx.android.synthetic.main.layout_images_search_view.tv_images_search_error
import javax.inject.Inject

class SearchImagesViewFragment : MvpAppCompatFragment(), SearchImagesView {

    @Inject
    lateinit var imagesInteractor: ImagesInteractor
    @Inject
    lateinit var applicationProvider: ApplicationProvider


    @InjectPresenter
    lateinit var searchImagesPresenter: SearchImagesPresenter

    private var searchImagesAdapter = SearchImagesAdapter()

    init {
        TheApplication.getAppComponent().inject(this)
    }

    @ProvidePresenter
    internal fun providePresenter(): SearchImagesPresenter {
        return SearchImagesPresenter(imagesInteractor)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        val view = inflater.inflate(R.layout.layout_images_search_view, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv_images_search_list.adapter = searchImagesAdapter
        rv_images_search_list.layoutManager = GridLayoutManager(context, 4)
        rv_images_search_list.addOnItemTouchListener(RecyclerViewOnItemClickListener(applicationProvider.applicationContext, rv_images_search_list, object: RecyclerViewOnItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val item: SearchImagesImageData?  = searchImagesAdapter.currentList?.get(position)
                if (item != null) {
                    searchImagesPresenter.onImageIsClicked(item)
                }
            }

            override fun onLongItemClick(view: View?, position: Int) {
            }
        }))


        tv_images_search_error.setOnClickListener { searchImagesPresenter.onErrorClicked()}
        sv_images_search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                searchImagesPresenter.onSearchTextChanged(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchImagesPresenter.onSearchTextSubmitted(query)
                return true
            }

        })

    }

    override fun updateDisplayData(data: PagedList<SearchImagesImageData>) {
        searchImagesAdapter.submitList(data)
    }

    override fun clearList() {
        searchImagesAdapter = SearchImagesAdapter()
        rv_images_search_list.adapter = searchImagesAdapter
        searchImagesAdapter.notifyDataSetChanged()
    }

    override fun showGeneralError(show: Boolean) {
        tv_images_search_error.setText(R.string.error_general)
        tv_images_search_error.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showNoNetworkError(show: Boolean) {
        tv_images_search_error.setText(R.string.error_no_network)
        tv_images_search_error.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        pb_images_search.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }
}