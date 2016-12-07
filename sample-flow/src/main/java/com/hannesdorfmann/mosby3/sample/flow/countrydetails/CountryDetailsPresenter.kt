package com.hannesdorfmann.mosby3.sample.flow.countries

import android.util.Log
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.sample.flow.countrydetails.CountryDetailsView
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
class CountryDetailsPresenter @Inject constructor(val atlas: Atlas) : MvpBasePresenter<CountryDetailsView>() {

  var subscription: Subscription? = null

  fun loadDetails(id : Int) {

    Log.d("Flow", "loadDetails()")

    view?.showLoading(false)

    subscription = atlas.getDetails(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
              Log.d("Flow", "view.setData()")
              view?.setData(it)
            },
            {
              Log.d("Flow", "view.showError()")
              view?.showError(it, false)
            },
            {
              Log.d("Flow", "view.showContent()")
              view?.showContent()
            }
        )

  }

  override fun attachView(view: CountryDetailsView?) {
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