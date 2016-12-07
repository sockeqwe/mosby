package com.hannesdorfmann.mosby3.sample.flow.countries

import android.util.Log
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.sample.flow.model.Atlas
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 *
 *
 * @author Hannes Dorfmann
 */
class CountriesPresenter @Inject constructor(val atlas: Atlas) : MvpBasePresenter<CountriesView>() {

  var subscription: Subscription? = null

  fun loadCountries(pullToRefresh: Boolean) {

    Log.d("Flow", "loadCountries($pullToRefresh)")

    view?.showLoading(pullToRefresh)

    subscription = atlas.getCountries()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
              Log.d("Flow", "view.setData()")
              view?.setData(it)
            },
            {
              Log.d("Flow", "view.showError()")
              view?.showError(it, pullToRefresh)
            },
            {
              Log.d("Flow", "view.showContent()")
              view?.showContent()
            }
        )

  }

  override fun attachView(view: CountriesView?) {
    super.attachView(view)
    Log.d("Flow", "attaching View to $this")
  }

  override fun detachView(retainInstance: Boolean) {
    Log.d("Flow", "detaching View from $this retained: $retainInstance")
    Log.d("Flow", "-------------------")
    super.detachView(retainInstance)
    if (!retainInstance) {
      subscription?.unsubscribe()
    }
  }
}