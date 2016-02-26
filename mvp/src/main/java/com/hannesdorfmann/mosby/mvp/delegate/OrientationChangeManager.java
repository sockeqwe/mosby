package com.hannesdorfmann.mosby.mvp.delegate;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.SparseArrayCompat;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * A internal class responsible to save internal presenter instances during screen orientation
 * changes and reattach the presenter afterwards.
 *
 * <p>
 * The idea is that each MVP View (based on ViewGroup) will get a unique view id. This view id is
 * used to store the presenter and viewstate in it. After screen orientation changes we can reuse
 * the presenter and viewstate by querying for the given view id (must be saved in view's state
 * somehow).
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 3.0
 */
class OrientationChangeManager<V extends MvpView, P extends MvpPresenter<V>> {

  static final String FRAGMENT_TAG = "com.hannesdorfmann.mosby.mvp.OrientationChangeFragment";

  /**
   * Never use this directly. Always use {@link #getFragment(Context)}
   */
  private OrientationChangeFragment internalFragment = null;

  /**
   * Get the next (mosby internal) view id
   *
   * @param context The context
   * @return the view id
   */
  @UiThread int nextViewId(Context context) {
    return getFragment(context).nextViewId();
  }

  private FragmentActivity getActivity(Context context) {
    while (context instanceof ContextWrapper) {
      if (context instanceof FragmentActivity) {
        return (FragmentActivity) context;
      }
      context = ((ContextWrapper) context).getBaseContext();
    }
    throw new IllegalStateException(
        "Could not find the surrounding FragmentActivity. Does your activity extends from android.support.v4.app.FragmentActivity like android.support.v7.app.AppCompatActivity ?");
  }

  /**
   * Get the internalFragment or creates a new one
   *
   * @param context The context
   * @return The internalFragment
   */
  @UiThread private OrientationChangeFragment getFragment(Context context) {

    if (internalFragment != null) {
      return internalFragment;
    }

    FragmentActivity activity = getActivity(context);

    OrientationChangeFragment fragment =
        (OrientationChangeFragment) activity.getSupportFragmentManager()
            .findFragmentByTag(FRAGMENT_TAG);

    // Already existing Fragment found
    if (fragment != null) {
      this.internalFragment = fragment;
      return fragment;
    }

    // No internalFragment found, so create a new one
    this.internalFragment = new OrientationChangeFragment();
    activity.getSupportFragmentManager()
        .beginTransaction()
        .add(internalFragment, FRAGMENT_TAG)
        .commit();

    return this.internalFragment;
  }

  /**
   * Get the presenter for the given view id
   *
   * @param id the id of the view (assigned internally)
   * @param context the context
   * @return The Presenter or null
   */
  @UiThread public P getPresenter(int id, @NonNull Context context) {

    OrientationChangeFragment fragment = getFragment(context);

    CacheEntry<V, P> entry = fragment.get(id);
    return entry == null ? null : entry.presenter;
  }

  /**
   * Get the presenter for the given view id
   *
   * @param id the id of the view (assigned internally)
   * @param context the context
   * @return The Presenter or null
   */
  @UiThread public <T> T getViewState(int id, @NonNull Context context) {

    OrientationChangeFragment fragment = getFragment(context);

    CacheEntry<V, P> entry = fragment.get(id);
    return entry == null ? null : (T) entry.viewState;
  }

  /**
   * Very important to avoid memory leaks!
   */
  @UiThread public void cleanUp() {
    internalFragment = null;
  }

  /**
   * Determines if the view will be destroyed permanently, because the whole activity will be
   * destroyed.
   *
   * @param context the context
   * @return true, if destroyed permanently, otherwise false
   */
  public boolean willViewBeDestroyedPermanently(Context context) {
    OrientationChangeFragment fragment = getFragment(context);
    return fragment.destroyed;
  }

  /**
   * Determines if the view will be detached from window (destroyed) because of an orientation
   * change
   *
   * @param context the context
   * @return true, if detached because of an orientation change. Otherwise, false
   */
  public boolean willViewBeDetachedBecauseOrientationChange(Context context) {
    OrientationChangeFragment fragment = getFragment(context);
    return fragment.stopped;
  }

  /**
   * Remove the Presenter and ViewState from internal {@link OrientationChangeFragment}
   *
   * @param viewId The (internal) view's id
   * @param context The context
   */
  public void removePresenterAndViewState(int viewId, Context context) {
    OrientationChangeFragment fragment = getFragment(context);
    fragment.remove(viewId);
  }

  /**
   * Puts the presenter in the internal cache "associated" with the given view id
   *
   * @param viewId The (interals) view id
   * @param presenter The presenter
   * @param context the context
   */
  public void putPresenter(int viewId, P presenter, Context context) {
    OrientationChangeFragment fragment = getFragment(context);
    CacheEntry<V, P> entry = fragment.get(viewId);
    if (entry == null) {
      entry = new CacheEntry<V, P>(presenter);
      fragment.put(viewId, entry);
    } else {
      entry.presenter = presenter;
    }
  }

  /**
   * Save the view state "in memory cache" during screen orientation changes
   * @param viewId The view id (mosby internal)
   * @param viewState The view state to save
   * @param context The context
   */
  public void putViewState(int viewId, Object viewState, Context context) {
    OrientationChangeFragment fragment = getFragment(context);
    CacheEntry<V, P> entry = fragment.get(viewId);
    if (entry == null) {
      throw new IllegalStateException(
          "Try to put the ViewState into cache. However, the presenter hasn't been put into cache before. This is not allowed. Ensure that the presenter is saved before putting the ViewState into cache.");
    } else {
      entry.viewState = viewState;
    }
  }

  /**
   * Internal config change Cache entry
   */
  static final class CacheEntry<V extends MvpView, P extends MvpPresenter<V>> {
    P presenter;
    Object viewState; // workaround: dont want to introduce dependency to viewstate module

    public CacheEntry(P presenter) {
      this.presenter = presenter;
    }
  }

  /**
   * Fragment internally used to deal with screen orientation changes
   *
   * @author Hannes Dorfmann
   * @since 3.0
   */
  public static final class OrientationChangeFragment extends Fragment {

    // 0 is preserverd as "no view id"
    private int VIEW_ID = 0;
    private SparseArrayCompat<CacheEntry> cache = new SparseArrayCompat<>();
    private boolean stopped = true;
    private boolean destroyed = false;

    /**
     * Get a vlaue from cache
     *
     * @param key the key
     * @param <T> The retrurn type
     * @return the cache entry. Is <code>null</code> if no entry found for the given key
     */
    <T> T get(int key) {
      return (T) cache.get(key);
    }

    /**
     * Put value into the cache
     *
     * @param key the key
     * @param entry the cache entry
     */
    void put(int key, CacheEntry entry) {
      cache.put(key, entry);
    }

    /**
     * Remove value from key
     *
     * @param key The key
     */
    void remove(int key) {
      cache.remove(key);
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRetainInstance(true);
    }

    @Override public void onDestroy() {
      destroyed = true;
      cache.clear();
      cache = null;

      super.onDestroy();
    }

    @Override public void onStart() {
      super.onStart();
      stopped = false;
    }

    @Override public void onStop() {
      super.onStop();
      stopped = true;
    }

    /**
     * Get the next (mosby internal) view id
     *
     * @return view id
     */
    int nextViewId() {
      while (cache.get(++VIEW_ID) != null) {
        if (VIEW_ID == Integer.MAX_VALUE) {
          throw new IllegalStateException(
              "Oops, it seems that we ran out of (mosby internal) view id's. It seems that your user has navigated more than "
                  + Integer.MAX_VALUE
                  + " times through your app. There is nothing you can do to fix that");
        }
      }
      return VIEW_ID;
    }
  }
}



