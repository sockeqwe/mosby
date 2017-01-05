/*
 * Copyright 2016 Hannes Dorfmann.
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

package com.hannesdorfmann.mosby3.sample.mvi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.MainMenuItem;
import com.hannesdorfmann.mosby3.sample.mvi.dependencyinjection.DependencyInjection;
import com.hannesdorfmann.mosby3.sample.mvi.view.category.CategoryFragment;
import com.hannesdorfmann.mosby3.sample.mvi.view.home.HomeFragment;
import com.hannesdorfmann.mosby3.sample.mvi.view.menu.MenuViewState;
import com.hannesdorfmann.mosby3.sample.mvi.view.search.SearchFragment;
import com.hannesdorfmann.mosby3.sample.mvi.view.selectedcounttoolbar.SelectedCountToolbar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

  private static final String KEY_TOOLBAR_TITLE = "toolbarTitle";

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.drawerLayout) DrawerLayout drawer;
  @BindView(R.id.sliding_layout) SlidingUpPanelLayout slidingUpPanel;
  private Unbinder unbinder;
  private Disposable disposable;
  private String title;
  private PublishSubject<Boolean> clearSelectionRelay;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    unbinder = ButterKnife.bind(this);
    toolbar.setTitle("Mosby MVI");
    toolbar.inflateMenu(R.menu.activity_main_toolbar);
    toolbar.setOnMenuItemClickListener(item -> {

      getSupportFragmentManager().beginTransaction()
          .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
              android.R.anim.fade_in, android.R.anim.fade_out)
          .add(R.id.drawerLayout, new SearchFragment())
          .addToBackStack("Search")
          .commit();
      return true;
    });

    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    if (savedInstanceState == null) {
      showCategoryItems(MainMenuItem.HOME);
    } else {
      title = savedInstanceState.getString(KEY_TOOLBAR_TITLE);
      toolbar.setTitle(title);
    }

    // TODO Create a Presenter & ViewState for this Activity
    DependencyInjection dependencyInjection = SampleApplication.getDependencyInjection(this);
    disposable = dependencyInjection.getMainMenuPresenter()
        .getViewStateObservable()
        .filter(state -> state instanceof MenuViewState.DataState)
        .cast(MenuViewState.DataState.class)
        .map(this::findSelectedMenuItem)
        .subscribe(this::showCategoryItems);
    clearSelectionRelay = dependencyInjection.getClearSelectionRelay();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
    disposable.dispose();
    Timber.d("------- Destoryed -------");
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_TOOLBAR_TITLE, toolbar.getTitle().toString());
  }

  private String findSelectedMenuItem(MenuViewState.DataState state) {
    for (MainMenuItem item : state.getCategories())
      if (item.isSelected()) return item.getName();

    throw new IllegalStateException("No category is selected in Main Menu" + state);
  }

  @Override public void onBackPressed() {
    SelectedCountToolbar selectedCountToolbar =
        (SelectedCountToolbar) findViewById(R.id.selectedCountToolbar);
    if (!closeDrawerIfOpen()) {
      if (selectedCountToolbar.getVisibility() == View.VISIBLE) {
        clearSelectionRelay.onNext(true);
      } else if (!closeSlidingUpPanelIfOpen()) super.onBackPressed();
    }
  }

  private boolean closeSlidingUpPanelIfOpen() {
    if (slidingUpPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
      slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
      return true;
    }
    return false;
  }

  private boolean closeDrawerIfOpen() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
      return true;
    }
    return false;
  }

  public void showCategoryItems(String categoryName) {
    closeDrawerIfOpen();
    String currentCategory = toolbar.getTitle().toString();
    if (!currentCategory.equals(categoryName)) {
      toolbar.setTitle(categoryName);
      Fragment f;
      if (categoryName.equals(MainMenuItem.HOME)) {
        f = new HomeFragment();
      } else {
        f = CategoryFragment.newInstance(categoryName);
      }
      getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, f).commit();
    }
  }
}
