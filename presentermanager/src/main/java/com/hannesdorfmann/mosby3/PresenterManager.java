package com.hannesdorfmann.mosby3;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import java.util.Map;
import java.util.UUID;

/**
 * A internal class responsible to save internal presenter instances during screen orientation
 * changes and reattach the presenter afterwards.
 *
 * <p>
 * The idea is that each MVP View (like a Activity, Fragment, ViewGroup) will get a unique view id.
 * This view id is
 * used to store the presenter and viewstate in it. After screen orientation changes we can reuse
 * the presenter and viewstate by querying for the given view id (must be saved in view's state
 * somehow).
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 3.0
 */
final public class PresenterManager {

  public static boolean DEBUG = false;
  public static final String DEBUG_TAG = "PresenterManager";
  final static String KEY_ACTIVITY_ID = "com.hannesdorfmann.mosby3.MosbyPresenterManagerActivityId";

  private final static Map<Activity, String> activityIdMap = new ArrayMap<>();
  private final static Map<String, ActivityScopedCache> activityScopedCacheMap = new ArrayMap<>();

  static final Application.ActivityLifecycleCallbacks activityLifecycleCallbacks =
      new Application.ActivityLifecycleCallbacks() {
        @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
          if (savedInstanceState != null) {
            String activityId = savedInstanceState.getString(KEY_ACTIVITY_ID);
            if (activityId != null) {
              // After a screen orientation change we map the newly created Activity to the same
              // Activity ID as the previous activity has had (before screen orientation change)
              activityIdMap.put(activity, activityId);
            }
          }
        }

        @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
          // Save the activityId into bundle so that the other
          String activityId = activityIdMap.get(activity);
          if (activityId != null) {
            outState.putString(KEY_ACTIVITY_ID, activityId);
          }
        }

        @Override public void onActivityStarted(Activity activity) {
        }

        @Override public void onActivityResumed(Activity activity) {
        }

        @Override public void onActivityPaused(Activity activity) {

        }

        @Override public void onActivityStopped(Activity activity) {
        }

        @Override public void onActivityDestroyed(Activity activity) {
          if (!activity.isChangingConfigurations()) {
            // Activity will be destroyed permanently, so reset the cache
            String activityId = activityIdMap.get(activity);
            if (activityId != null) {
              ActivityScopedCache scopedCache = activityScopedCacheMap.get(activityId);
              if (scopedCache != null) {
                scopedCache.clear();
                activityScopedCacheMap.remove(activityId);
              }

              // No Activity Scoped cache available, so unregister
              if (activityScopedCacheMap.isEmpty()) {
                // All Mosby related activities are destroyed, so we can remove the activity lifecylce listener
                activity.getApplication()
                    .unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
                if (DEBUG) {
                  Log.d(DEBUG_TAG, "Unregistering ActivityLifecycleCallbacks");
                }
              }
            }
          }
          activityIdMap.remove(activity);
        }
      };

  private PresenterManager() {
    throw new RuntimeException("Not instantiatable!");
  }

  /**
   * Get an already existing {@link ActivityScopedCache} or creates a new one if not existing yet
   *
   * @param activity The Activitiy for which you want to get the activity scope for
   * @return The {@link ActivityScopedCache} for the given Activity
   */
  @NonNull @MainThread static ActivityScopedCache getOrCreateActivityScopedCache(
      @NonNull Activity activity) {
    if (activity == null) {
      throw new NullPointerException("Activity is null");
    }

    String activityId = activityIdMap.get(activity);
    if (activityId == null) {
      // Activity not registered yet
      activityId = UUID.randomUUID().toString();
      activityIdMap.put(activity, activityId);

      if (activityIdMap.size() == 1) {
        // Added the an Activity for the first time so register Activity LifecycleListener
        activity.getApplication().registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        if (DEBUG) {
          Log.d(DEBUG_TAG, "Registering ActivityLifecycleCallbacks");
        }
      }
    }

    ActivityScopedCache activityScopedCache = activityScopedCacheMap.get(activityId);
    if (activityScopedCache == null) {
      activityScopedCache = new ActivityScopedCache();
      activityScopedCacheMap.put(activityId, activityScopedCache);
    }

    return activityScopedCache;
  }

  /**
   * Get the  {@link ActivityScopedCache} for the given Activity or <code>null</code> if no {@link
   * ActivityScopedCache} exists for the given Activity
   *
   * @param activity The activity
   * @return The {@link ActivityScopedCache} or null
   * @see #getOrCreateActivityScopedCache(Activity)
   */
  @Nullable @MainThread static ActivityScopedCache getActivityScope(@NonNull Activity activity) {
    if (activity == null) {
      throw new NullPointerException("Activity is null");
    }
    String activityId = activityIdMap.get(activity);
    if (activityId == null) {
      return null;
    }

    return activityScopedCacheMap.get(activityId);
  }

  /**
   * Get the presenter for the View with the given (Mosby - internal) view Id or <code>null</code>
   * if no presenter for the given view (via view id) exists.
   *
   * @param activity The Activity (used for scoping)
   * @param viewId The mosby internal View Id (unique among all {@link MvpView}
   * @param <P> The Presenter type
   * @return The Presenter or <code>null</code>
   */
  @Nullable public static <P> P getPresenter(@NonNull Activity activity, @NonNull String viewId) {
    if (activity == null) {
      throw new NullPointerException("Activity is null");
    }

    if (viewId == null) {
      throw new NullPointerException("View id is null");
    }

    ActivityScopedCache scopedCache = getActivityScope(activity);
    return scopedCache == null ? null : (P) scopedCache.getPresenter(viewId);
  }

  /**
   * Get the ViewState (see mosby viestate modlue) for the View with the given (Mosby - internal)
   * view Id or <code>null</code>
   * if no viewstate for the given view exists.
   *
   * @param activity The Activity (used for scoping)
   * @param viewId The mosby internal View Id (unique among all {@link MvpView}
   * @param <VS> The type of the ViewState type
   * @return The Presenter or <code>null</code>
   */
  @Nullable public static <VS> VS getViewState(@NonNull Activity activity, @NonNull String viewId) {
    if (activity == null) {
      throw new NullPointerException("Activity is null");
    }

    if (viewId == null) {
      throw new NullPointerException("View id is null");
    }

    ActivityScopedCache scopedCache = getActivityScope(activity);
    return scopedCache == null ? null : (VS) scopedCache.getViewState(viewId);
  }

  /**
   * Get the Activity of a context. This is typically used to determine the hosting activity of a
   * {@link View}
   *
   * @param context The context
   * @return The Activity or throws an Exception if Activity couldnt be determined
   */
  @NonNull public static Activity getActivity(@NonNull Context context) {
    if (context == null) {
      throw new NullPointerException("context == null");
    }
    if (context instanceof Activity) {
      return (Activity) context;
    }

    while (context instanceof ContextWrapper) {
      if (context instanceof Activity) {
        return (Activity) context;
      }
      context = ((ContextWrapper) context).getBaseContext();
    }
    throw new IllegalStateException("Could not find the surrounding Activity");
  }

  /**
   * Clears the internal (static) state. Used for testing.
   */
  static void reset() {
    activityIdMap.clear();
    for (ActivityScopedCache scopedCache : activityScopedCacheMap.values()) {
      scopedCache.clear();
    }

    activityScopedCacheMap.clear();
  }

  /**
   * Puts the presenter into the internal cache
   *
   * @param activity The parent activity
   * @param viewId the view id (mosby internal)
   * @param presenter the presenter
   */
  public static void putPresenter(@NonNull Activity activity, @NonNull String viewId,
      @NonNull MvpPresenter<? extends MvpView> presenter) {
    if (activity == null) {
      throw new NullPointerException("Activity is null");
    }

    ActivityScopedCache scopedCache = getOrCreateActivityScopedCache(activity);
    scopedCache.putPresenter(viewId, presenter);
  }

  /**
   * Puts the presenter into the internal cache
   *
   * @param activity The parent activity
   * @param viewId the view id (mosby internal)
   * @param viewState the presenter
   */
  public static void putViewState(@NonNull Activity activity, @NonNull String viewId,
      @NonNull Object viewState) {
    if (activity == null) {
      throw new NullPointerException("Activity is null");
    }

    ActivityScopedCache scopedCache = getOrCreateActivityScopedCache(activity);
    scopedCache.putViewState(viewId, viewState);
  }

  /**
   * Removes the Presenter (and ViewState) for the given View. Does nothing if no Presenter is
   * stored internally with the given viewId
   *
   * @param activity The activity
   * @param viewId The mosby internal view id
   */
  public static void remove(@NonNull Activity activity, @NonNull String viewId) {
    if (activity == null) {
      throw new NullPointerException("Activity is null");
    }

    ActivityScopedCache activityScope = getActivityScope(activity);
    if (activityScope != null) {
      activityScope.remove(viewId);
    }
  }
}



