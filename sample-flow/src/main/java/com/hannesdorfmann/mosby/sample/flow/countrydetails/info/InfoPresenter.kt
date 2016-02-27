package com.hannesdorfmann.mosby.sample.flow.countries

import android.util.Log
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby.sample.flow.countrydetails.info.InfoView
import com.hannesdorfmann.mosby.sample.flow.model.Atlas
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 *
 *
 * @author Hannes Dorfmann
 */
class InfoPresenter @Inject constructor(val atlas: Atlas) : MvpBasePresenter<InfoView>() {

  var subscription: Subscription? = null

  fun loadInfo(pullToRefresh: Boolean, id: Int) {

    Log.d("Flow", "InfoPresenter loadInfo()")

    view?.showLoading(pullToRefresh)

    subscription = atlas.getInfo(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
              Log.d("Flow", "InfoPresenter view.setData()")
              view?.setData(it)
            },
            {
              Log.d("Flow", "InfoPresenter view.showError()")
              view?.showError(it, pullToRefresh)
            },
            {
              Log.d("Flow", "InfoPresenter view.showContent()")
              view?.showContent()
            }
        )

  }

  override fun attachView(view: InfoView) {
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