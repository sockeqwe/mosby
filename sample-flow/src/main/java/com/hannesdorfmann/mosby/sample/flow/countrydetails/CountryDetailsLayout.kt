package com.hannesdorfmann.mosby.sample.flow.countries

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import butterknife.bindView
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState
import com.hannesdorfmann.mosby.mvp.viewstate.layout.MvpViewStateFrameLayout
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState
import com.hannesdorfmann.mosby.sample.flow.AtlasApplication
import com.hannesdorfmann.mosby.sample.flow.R
import com.hannesdorfmann.mosby.sample.flow.countrydetails.CountryDetailsPagerAdapter
import com.hannesdorfmann.mosby.sample.flow.countrydetails.CountryDetailsView
import com.hannesdorfmann.mosby.sample.flow.model.CountryDetail
import com.squareup.picasso.Picasso
import flow.Flow

/**
 *
 *
 * @author Hannes Dorfmann
 */
class CountryDetailsLayout(c: Context, atts: AttributeSet) : CountryDetailsView, MvpViewStateFrameLayout<CountryDetailsView, CountryDetailsPresenter>(
    c, atts) {


  private val viewPager: ViewPager by bindView(R.id.viewPager)
  private val contentView: View by bindView(R.id.contentView)
  private val errorView: View by bindView(R.id.errorView)
  private val loadingView: View by bindView(R.id.loadingView)
  private val highlightImage: ImageView by bindView(R.id.highlightImage)
  private val colapsingToolbar: CollapsingToolbarLayout by bindView(R.id.collapsingToolbar)
  private val toolbar: Toolbar by bindView(R.id.toolbar)
  private val tabs: TabLayout by bindView(R.id.tabs)

  private val countryId by lazy {
    val countriesScreen: CountryDetailsScreen = Flow.getKey(this)!!
    countriesScreen.countryId
  }

  init {
    isRetainInstance = true
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha)
    toolbar.setNavigationOnClickListener {
      Flow.get(this).goBack()
    }
    highlightImage.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.image_highlight_darking), PorterDuff.Mode.SRC_ATOP)
    errorView.setOnClickListener {
      loadData(false)
    }
  }

  override fun createPresenter(): CountryDetailsPresenter = AtlasApplication.getComponent(
      context).countryDetailsPresenter()

  override fun createViewState(): ViewState<CountryDetailsView> = RetainingLceViewState<CountryDetail, CountryDetailsView>()

  override fun onNewViewStateInstance() = loadData(false)

  override fun showLoading(pullToRefresh: Boolean) {
    castedViewState().setStateShowLoading(pullToRefresh)

    loadingView.visibility = VISIBLE
    errorView.visibility = GONE
    contentView.visibility = GONE

  }

  override fun showContent() {

    // TODO create ViewPager adapter

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

  override fun setData(data: CountryDetail) {

    Picasso.with(context).load(data.imageUrl).placeholder(
        R.color.image_placeholder).fit().centerCrop().into(highlightImage)
    colapsingToolbar.title = data.name
    toolbar.title = data.name

    val adapter = CountryDetailsPagerAdapter(context, data.tabs)
    viewPager.adapter = adapter
    tabs.setupWithViewPager(viewPager)
  }

  override fun loadData(pullToRefresh: Boolean) = presenter.loadDetails(countryId)

  private inline fun castedViewState() = viewState as RetainingLceViewState<CountryDetail, CountryDetailsView>

}