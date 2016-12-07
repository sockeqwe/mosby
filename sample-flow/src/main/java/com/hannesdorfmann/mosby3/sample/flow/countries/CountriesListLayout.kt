package com.hannesdorfmann.mosby3.sample.flow.countries

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import butterknife.bindView
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState
import com.hannesdorfmann.mosby3.mvp.viewstate.layout.MvpViewStateFrameLayout
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.RetainingLceViewState
import com.hannesdorfmann.mosby3.sample.flow.AtlasApplication
import com.hannesdorfmann.mosby3.sample.flow.R
import com.hannesdorfmann.mosby3.sample.flow.dpToPx
import com.hannesdorfmann.mosby3.sample.flow.model.Country
import flow.Flow

/**
 *
 *
 * @author Hannes Dorfmann
 */
class CountriesListLayout(c: Context, atts: AttributeSet) : CountriesView, MvpViewStateFrameLayout<CountriesView, CountriesPresenter>(
    c, atts) {


  private val recyclerView: RecyclerView by bindView(R.id.recyclerView)
  private val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.swipeRefreshLayout)
  private val errorView: View by bindView(R.id.errorView)
  private val loadingView: View by bindView(R.id.loadingView)

  private val adapter = CountriesAdapter(LayoutInflater.from(context),
      { // On click navigate to details screen
        Flow.get(this).set(CountryDetailsScreen(it.id))
      })

  init {
    LayoutInflater.from(context).inflate(R.layout.recycler_swiperefresh_view, this, true)
    isRetainInstance = true

    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(context)

    errorView.setOnClickListener {
      loadData(false)
    }

    swipeRefreshLayout.setOnRefreshListener {
      loadData(true)
    }
    swipeRefreshLayout.setProgressViewOffset(true, 0, dpToPx(58f).toInt())
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
      swipeRefreshLayout.visibility = VISIBLE
      swipeRefreshLayout.post {
        swipeRefreshLayout.isRefreshing = true
      }
    } else {
      loadingView.visibility = VISIBLE
      errorView.visibility = GONE
      swipeRefreshLayout.visibility = GONE
    }
  }

  override fun showContent() {

    castedViewState().setStateShowContent(adapter.items)

    if (isRestoringViewState) {
      swipeRefreshLayout.visibility = VISIBLE
      errorView.visibility = GONE
      loadingView.visibility = GONE
    } else {
      swipeRefreshLayout.alpha = 0f
      swipeRefreshLayout.visibility = VISIBLE
      swipeRefreshLayout.animate().alpha(1f).start()
      loadingView.animate().alpha(
          0f).withEndAction { loadingView.visibility = GONE; loadingView.alpha = 1f }
          .start()

      errorView.visibility = GONE
    }

    swipeRefreshLayout.isRefreshing = false

  }

  override fun showError(e: Throwable?, pullToRefresh: Boolean) {

    castedViewState().setStateShowError(e, pullToRefresh)

    if (pullToRefresh) {
      swipeRefreshLayout.visibility = VISIBLE
      errorView.visibility = GONE
      loadingView.visibility = GONE
      Toast.makeText(context, "An error has occurred", Toast.LENGTH_SHORT).show()
    } else {
      swipeRefreshLayout.visibility = GONE
      loadingView.visibility = GONE
      errorView.visibility = VISIBLE
    }
    swipeRefreshLayout.isRefreshing = false
  }

  override fun setData(data: List<Country>) {
    adapter.items = data
    adapter.notifyDataSetChanged()
  }

  override fun loadData(pullToRefresh: Boolean) = presenter.loadCountries(pullToRefresh)

  private inline fun castedViewState() = viewState as RetainingLceViewState<List<Country>, CountriesView>

}