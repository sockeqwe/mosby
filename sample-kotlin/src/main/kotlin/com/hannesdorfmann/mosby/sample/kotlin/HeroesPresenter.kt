package com.hannesdorfmann.mosby.sample.kotlin

import android.util.Log
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby.sample.kotlin.model.AsyncHeroesTask

/**
 * Presenter that loads list of heroes
 *
 * @author Hannes Dorfmann
 */
class HeroesPresenter : MvpBasePresenter<HeroesView> () {

    val TAG = "HeroesPresenter"

    private var loaderTask: AsyncHeroesTask ? = null

    fun loadHeroes(pullToRefresh: Boolean) {

        Log.d(TAG, "loadHeroes({$pullToRefresh})")

        cancelIfRunning();

        // Show Loading
        view?.showLoading(pullToRefresh)

        // execute loading
        loaderTask = AsyncHeroesTask(
                pullToRefresh,
                { heroes, pullToRefresh ->
                    view?.setData(heroes)
                    view?.showContent()
                },
                { exception, pullToRefresh ->
                    view?.showError(exception, pullToRefresh)
                }
        )

        loaderTask?.execute()

    }

    fun cancelIfRunning() {

        // Cancel any previous one
        loaderTask?.cancel(true);
    }

    override fun detachView(retainInstance: Boolean) {
        super.detachView(retainInstance)

        val builder = StringBuilder("detachView({$retainInstance})")

        if (!retainInstance) {
            cancelIfRunning()
            builder.append(" --> cancel async task")
        }

        Log.d(TAG, builder.toString())
    }

}