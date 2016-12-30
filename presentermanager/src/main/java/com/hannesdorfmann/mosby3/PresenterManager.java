package com.hannesdorfmann.mosby3;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import java.util.Map;
import java.util.UUID;

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
final class PresenterManager<V extends MvpView, P extends MvpPresenter<V>> {

  private static final String FRAGMENT_TAG =
      "com.hannesdorfmann.mosby3.mvp.PresenterManagerFragment";

  public static final boolean DEBUG = true;
  private static final String DEBUG_TAG = "PresenterManager";
  /**
   * Never use this directly. Always use {@link #getFragmentOrCreate(Context)}
   */
  private PresenterManagerFragment internalFragment = null;

  // package private constructor
  PresenterManager() {
  }

  /**
   * Get the next (mosby internal) view id
   *
   * @param context The context
   * @return the view id
   */
  @UiThread @NonNull String nextViewId(Context context) {
    return getFragmentOrCreate(context).nextViewId();
  }

  @NonNull private FragmentActivity getActivity(@NonNull Context context) {
    if (context == null) {
      throw new NullPointerException("context == null");
    }
    if (context instanceof FragmentActivity) {
      return (FragmentActivity) context;
    }

    while (context instanceof ContextWrapper) {
      if (context instanceof FragmentActivity) {
        return (FragmentActivity) context;
      }
      context = ((ContextWrapper) context).getBaseContext();
    }
    throw new IllegalStateException(
        "Could not find the surrounding FragmentActivity. Does your activity extends from android.support.v4.app.FragmentActivity like android.support.v7.app.AppCompatActivity ?");
  }

  @Nullable @UiThread
  private PresenterManagerFragment getFragmentIfNotClearedYet(@NonNull Context context) {

    if (internalFragment != null) {
      if (DEBUG) {
        Log.d(DEBUG_TAG, "internalFragment precached " + internalFragment);
      }
      return internalFragment;
    }

    FragmentActivity activity = getActivity(context);

    PresenterManagerFragment fragment =
        (PresenterManagerFragment) activity.getSupportFragmentManager()
            .findFragmentByTag(FRAGMENT_TAG);

    // Already existing Fragment found
    if (fragment != null) {
      this.internalFragment = fragment;
      if (DEBUG) {
        Log.d(DEBUG_TAG, "internalFragment found in FragmentManager " + internalFragment);
      }
      return fragment;
    }

    return null;
  }

  /**
   * Get the internalFragment or creates a new one
   *
   * @param context The context
   * @return The internalFragment
   */
  @UiThread @NonNull private PresenterManagerFragment getFragmentOrCreate(Context context) {

    PresenterManagerFragment fragment = getFragmentIfNotClearedYet(context);
    if (fragment != null) {
      return fragment;
    }

    // No internalFragment found, so create a new one
    this.internalFragment = new PresenterManagerFragment();
    FragmentActivity activity = getActivity(context);
    activity.getSupportFragmentManager()
        .beginTransaction()
        .add(internalFragment, FRAGMENT_TAG)
        .commit(); // TODO should be commitNow() ?

    if (DEBUG) {
      Log.d(DEBUG_TAG,
          "internalFragment new created and put to FragmentManager " + internalFragment);
    }
    return this.internalFragment;
  }

  /**
   * Get the presenter for the given view id
   *
   * @param viewId the id of the view (assigned internally)
   * @param context the context
   * @return The Presenter or null
   */
  @UiThread P getPresenter(String viewId, @NonNull Context context) {

    PresenterManagerFragment fragment = getFragmentOrCreate(context);

    CacheEntry<V, P> entry = fragment.get(viewId);
    return entry == null ? null : entry.presenter;
  }

  /**
   * Get the presenter for the given view id
   *
   * @param viewId the id of the view (assigned internally)
   * @param context the context
   * @return The Presenter or null
   */
  @UiThread public <T> T getViewState(String viewId, @NonNull Context context) {

    PresenterManagerFragment fragment = getFragmentOrCreate(context);

    CacheEntry<V, P> entry = fragment.get(viewId);
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
    return !getActivity(context).isChangingConfigurations();
  }

  /**
   * Determines if the view will be detached from window (destroyed) because of an orientation
   * change
   *
   * @param context the context
   * @return true, if detached because of an orientation change. Otherwise, false
   */
  public boolean willViewBeDetachedBecauseOrientationChange(Context context) {
    return getActivity(context).isChangingConfigurations();
  }

  /**
   * Remove the Presenter and ViewState from internal {@link PresenterManagerFragment}
   *
   * @param viewId The (internal) view's id
   * @param context The context
   */
  public void removePresenterAndViewState(String viewId, Context context) {
    PresenterManagerFragment fragment = getFragmentIfNotClearedYet(context);
    if (fragment != null) {
      fragment.remove(viewId);
    }
    // Otherwise presenter cache has already been cleared by the fragment itself
    // and therefore the presenter is already removed from internal cache
  }

  /**
   * Puts the presenter in the internal cache "associated" with the given view id
   *
   * @param viewId The (interals) view id
   * @param presenter The presenter
   * @param context the context
   */
  public void putPresenter(String viewId, P presenter, Context context) {
    PresenterManagerFragment fragment = getFragmentOrCreate(context);
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
   *
   * @param viewId The view id (mosby internal)
   * @param viewState The view state to save
   * @param context The context
   */
  public void putViewState(String viewId, Object viewState, Context context) {
    PresenterManagerFragment fragment = getFragmentOrCreate(context);
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
  public static final class PresenterManagerFragment extends Fragment {
    private Map<String, CacheEntry> cache = new ArrayMap<>();

    /**
     * Get a value from cache
     *
     * @param key the key
     * @param <T> The retrurn type
     * @return the cache entry. Is <code>null</code> if no entry found for the given key
     */
    <T> T get(String key) {
      return (T) cache.get(key);
    }

    /**
     * Put value into the cache
     *
     * @param key the key
     * @param entry the cache entry
     */
    void put(String key, CacheEntry entry) {
      cache.put(key, entry);
    }

    /**
     * Remove value from key
     *
     * @param key The key
     */
    void remove(String key) {
      if (cache != null) { // check if cache has already been cleared
        cache.remove(key);
      }
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRetainInstance(true);
    }

    @Override public void onDestroy() {
      //destroyed = true;
      cache.clear();
      cache = null;

      if (DEBUG) {
        Log.d(DEBUG_TAG, toString() + " internalFragment onDestroy() - clearing cache - " + this);
      }

      super.onDestroy();
    }

    @Override public void onStart() {
      super.onStart();
     /*
      if (DEBUG) {
        Log.d(DEBUG_TAG, toString() + " internalFragment onStart() " + this);
      }
      */
    }

    @Override public void onStop() {
      super.onStop();

      /*
      if (DEBUG) {
        Log.d(DEBUG_TAG, toString() + " internalFragment onStop() " + this);
      }
      */
    }

    /**
     * Get the next (mosby internal) view id
     *
     * @return view id
     */
    @NonNull String nextViewId() {
      String uuid;
      do {
        uuid = UUID.randomUUID().toString();
      } while (cache.get(uuid) != null);
      // Should never be the case because UUID is supposed to be "cryptographically strong",
      // but it won't hurt to check that in a do-while loop

      if (uuid == null || uuid.length() == 0) {
        throw new NullPointerException(
            "Oops, generated View Id is null. This should never be the case! Please file an issue at https://github.com/sockeqwe/mosby/issues");
      }
      return uuid;
    }
  }
}



