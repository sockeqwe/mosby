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
import com.hannesdorfmann.mosby.sample.flow.countrydetails.info.InfoView
import com.hannesdorfmann.mosby.sample.flow.dpToPx
import com.hannesdorfmann.mosby.sample.flow.model.InfoText

/**
 *
 *
 * @author Hannes Dorfmann
 */
class InfoLayout(c: Context, atts: AttributeSet?) : InfoView, MvpViewStateFrameLayout<InfoView, InfoPresenter>(
    c, atts) {

  constructor(c: Context) : this(c, null)


  private val recyclerView: RecyclerView by bindView(R.id.recyclerView)
  private val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.swipeRefreshLayout)
  private val errorView: View by bindView(R.id.errorView)
  private val loadingView: View by bindView(R.id.loadingView)

  var countryId: Int = 0

  private val adapter = InfoAdapter(LayoutInflater.from(context))

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


  override fun createPresenter(): InfoPresenter = AtlasApplication.getComponent(
      context).infoPresenter()

  override fun createViewState(): ViewState<InfoView> = RetainingLceViewState<List<InfoText>, InfoView>()

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

  override fun setData(data: List<InfoText>) {
    adapter.items = data
    adapter.notifyDataSetChanged()
  }

  override fun loadData(pullToRefresh: Boolean) = presenter.loadInfo(pullToRefresh, countryId)

  private inline fun castedViewState() = viewState as RetainingLceViewState<List<InfoText>, InfoView>
}