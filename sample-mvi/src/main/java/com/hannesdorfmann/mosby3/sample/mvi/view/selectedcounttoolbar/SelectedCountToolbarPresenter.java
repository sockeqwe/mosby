/*
 * Copyright 2017 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby3.sample.mvi.view.selectedcounttoolbar;

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

/**
 * @author Hannes Dorfmann
 */
public class SelectedCountToolbarPresenter
    extends MviBasePresenter<SelectedCountToolbarView, Integer> {

  private final Observable<Integer> selectedCountObservable;
  private final PublishSubject<Boolean> clearSelectionRelay;
  private final PublishSubject<Boolean> deleteSelectedItemsRelay;
  Disposable clearSelectionDisposal;
  Disposable deleteSelectedItemsDisposal;

  public SelectedCountToolbarPresenter(Observable<Integer> selectedCountObservable,
      PublishSubject<Boolean> clearSelectionRelay,
      PublishSubject<Boolean> deleteSelectedItemsRelay) {
    this.selectedCountObservable = selectedCountObservable;
    this.clearSelectionRelay = clearSelectionRelay;
    this.deleteSelectedItemsRelay = deleteSelectedItemsRelay;
  }

  @Override protected void bindIntents() {

    clearSelectionDisposal = intent(SelectedCountToolbarView::clearSelectionIntent)
        .doOnNext(ignore -> Timber.d("intent: clear selection"))
        .subscribe(aBoolean -> clearSelectionRelay.onNext(aBoolean));
    subscribeViewState(selectedCountObservable, SelectedCountToolbarView::render);

    deleteSelectedItemsDisposal =
        intent(SelectedCountToolbarView::deleteSelectedItemsIntent)
            .doOnNext(items -> Timber.d("intent: delete selected items "+items))
            .subscribe(aBoolean -> deleteSelectedItemsRelay.onNext(aBoolean));

    subscribeViewState(selectedCountObservable, SelectedCountToolbarView::render);
  }

  @Override protected void unbindIntents() {
    clearSelectionDisposal.dispose();
    deleteSelectedItemsDisposal.dispose();
  }
}
