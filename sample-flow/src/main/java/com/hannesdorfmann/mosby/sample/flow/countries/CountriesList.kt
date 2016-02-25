package com.hannesdorfmann.mosby.sample.flow.countries

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import butterknife.bindView
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState
import com.hannesdorfmann.mosby.mvp.viewstate.layout.MvpViewStateFrameLayout
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState
import com.hannesdorfmann.mosby.sample.flow.AtlasApplication
import com.hannesdorfmann.mosby.sample.flow.R
import com.hannesdorfmann.mosby.sample.flow.dpToPx
import com.hannesdorfmann.mosby.sample.flow.model.Country

/**
 *
 *
 * @author Hannes Dorfmann
 */
class CountriesList(c: Context, atts: AttributeSet) : CountriesView, MvpViewStateFrameLayout<CountriesView, CountriesPresenter>(
    c, atts) {


  private val recyclerView: RecyclerView by bindView(R.id.recyclerView)
  private val contentView: SwipeRefreshLayout by bindView(R.id.contentView)
  private val errorView: View by bindView(R.id.errorView)
  private val loadingView: View by bindView(R.id.loadingView)

  private val adapter = CountriesAdapter(LayoutInflater.from(context))

  init {
    LayoutInflater.from(context).inflate(R.layout.countriesview, this, true)
    isRetainInstance = true

    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(context)

    errorView.setOnClickListener {
      loadData(false)
    }

    contentView.setOnRefreshListener {
      loadData(true)
    }
    contentView.setProgressViewOffset(true, 0, dpToPx(54f).toInt())
  }

  override fun createPresenter(): CountriesPresenter = AtlasApplication.getComponent(
      context).countriesPresenter()

  override fun createViewState(): ViewState<CountriesView> = RetainingLceViewState<List<Country>, CountriesView>()

  override fun onNewViewStateInstance() = loadData(false)

  override fun showLoading(pullToRefresh: Boolean) {
    castedViewState().setStateShowLoading(pullToRefresh)

    if (pullToRefresh) {
      loadingView.visibility = GONE
      errorView.visibility = GONE
      contentView.visibility = VISIBLE
      contentView.post {
        contentView.isRefreshing = true
      }
    } else {
      loadingView.visibility = VISIBLE
      errorView.visibility = GONE
      contentView.visibility = GONE
    }
  }

  override fun showContent() {

    castedViewState().setStateShowContent(adapter.items)

    if (isRestoringViewState) {
      contentView.visibility = VISIBLE
      errorView.visibility = GONE
      loadingView.visibility = GONE
    } else {
      contentView.alpha = 0f
      contentView.visibility = VISIBLE
      contentView.animate().alpha(1f).start()
      loadingView.animate().alpha(
          0f).withEndAction { loadingView.visibility = GONE; loadingView.alpha = 1f }
          .start()

      errorView.visibility = GONE
    }

    contentView.isRefreshing = false
  }

  override fun showError(e: Throwable?, pullToRefresh: Boolean) {

    castedViewState().setStateShowError(e, pullToRefresh)

    if (pullToRefresh) {
      contentView.visibility = VISIBLE
      errorView.visibility = GONE
      loadingView.visibility = GONE
      Toast.makeText(context, "An error has occurred", Toast.LENGTH_SHORT).show()
    } else {
      contentView.visibility = GONE
      loadingView.visibility = GONE
      errorView.visibility = VISIBLE
    }
  }

  override fun setData(data: List<Country>) {
    adapter.items = data
    adapter.notifyDataSetChanged()
  }

  override fun loadData(pullToRefresh: Boolean) = presenter.loadCountries(pullToRefresh)

  private inline fun castedViewState() = viewState as RetainingLceViewState<List<Country>, CountriesView>
}