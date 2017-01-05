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

package com.hannesdorfmann.mosby3.sample.mvi.view.shoppingcartoverview;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.hannesdorfmann.mosby3.mvi.MviFragment;
import com.hannesdorfmann.mosby3.sample.mvi.R;
import com.hannesdorfmann.mosby3.sample.mvi.SampleApplication;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

/**
 * This class doesn't neccessarily has to be a fragment. It's just a fragment because I want to
 * demonstrate that mosby works with fragments in xml layouts too.
 *
 * @author Hannes Dorfmann
 */
public class ShoppingCartOverviewFragment
    extends MviFragment<ShoppingCartOverviewView, ShoppingCartOverviewPresenter>
    implements ShoppingCartOverviewView {

  private ShoppingCartOverviewAdapter adapter;
  private PublishSubject<Product> removeRelay = PublishSubject.create();
  private Unbinder unbinder;
  @BindView(R.id.shoppingCartRecyclerView) RecyclerView recyclerView;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
    unbinder = ButterKnife.bind(this, v);
    adapter = new ShoppingCartOverviewAdapter(getActivity());
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    setUpItemTouchHelper();
    return v;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @NonNull @Override public ShoppingCartOverviewPresenter createPresenter() {
    Timber.d("Create Presenter");
    return SampleApplication.getDependencyInjection(getActivity()).getShoppingCartPresenter();
  }

  @Override public Observable<Boolean> loadItemsIntent() {
    return Observable.just(true);
  }

  @Override public Observable<List<Product>> selectItemsIntent() {
    return adapter.selectedItemsObservable();
  }

  private void setUpItemTouchHelper() {

    //
    // Borrowed from https://github.com/nemanja-kovacevic/recycler-view-swipe-to-delete/blob/master/app/src/main/java/net/nemanjakovacevic/recyclerviewswipetodelete/MainActivity.java
    //

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
        new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

          // we want to cache these and not allocate anything repeatedly in the onChildDraw method
          Drawable background;
          Drawable xMark;
          int xMarkMargin;
          boolean initiated;

          private void init() {
            background =
                new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.delete_background));
            xMark = ContextCompat.getDrawable(getActivity(), R.drawable.ic_remove);
            xMarkMargin = (int) getActivity().getResources().getDimension(R.dimen.ic_clear_margin);
            initiated = true;
          }

          // not important, we don't want drag & drop
          @Override public boolean onMove(RecyclerView recyclerView,
              RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
          }

          @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int swipedPosition = viewHolder.getAdapterPosition();
            Product productAt = adapter.getProductAt(swipedPosition);
            removeRelay.onNext(productAt);
          }

          @Override public void onChildDraw(Canvas c, RecyclerView recyclerView,
              RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
              boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;

            // not sure why, but this method get's called for viewholder that are already swiped away
            if (viewHolder.getAdapterPosition() == -1) {
              // not interested in those
              return;
            }

            if (!initiated) {
              init();
            }

            // draw red background
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                itemView.getRight(), itemView.getBottom());
            background.draw(c);

            // draw x mark
            int itemHeight = itemView.getBottom() - itemView.getTop();
            int intrinsicWidth = xMark.getIntrinsicWidth();
            int intrinsicHeight = xMark.getIntrinsicWidth();

            int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
            int xMarkRight = itemView.getRight() - xMarkMargin;
            int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int xMarkBottom = xMarkTop + intrinsicHeight;
            xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

            // xMark.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
          }
        };
    ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
    mItemTouchHelper.attachToRecyclerView(recyclerView);
  }

  @Override public Observable<Product> removeItemIntent() {
    // DELEAY it for 500 miliseconds so that the user sees the swipe to delete animation
    return removeRelay.delay(500, TimeUnit.MILLISECONDS);
  }

  @Override public void render(List<ShoppingCartOverviewItem> itemsInShoppingCart) {
    Timber.d("Render %s ", itemsInShoppingCart);
    adapter.setItems(itemsInShoppingCart);
  }
}
