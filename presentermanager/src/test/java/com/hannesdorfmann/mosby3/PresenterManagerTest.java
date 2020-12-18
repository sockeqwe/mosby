package com.hannesdorfmann.mosby3;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * @author Hannes Dorfmann
 */
@PowerMockRunnerDelegate(RobolectricTestRunner.class) @RunWith(PowerMockRunner.class)
@PrepareForTest({ Activity.class }) @Config(manifest = Config.NONE)
public class PresenterManagerTest {

  @Before public void clear() {
    PresenterManager.reset();
  }

  @Test public void getOrCreateActivityScopeCacheThrowsNullpointerException() {
    try {
      PresenterManager.getOrCreateActivityScopedCache(null);
      Assert.fail("Exception expected");
    } catch (NullPointerException e) {
    }
  }


  @Test public void getActivityScopeCacheThrowsNullpointerException() {
    try {
      PresenterManager.getActivityScope(null);
      Assert.fail("Exception expected");
    } catch (NullPointerException e) {
    }
  }

  @Test public void getPresenterThrowsNullPointerException() {
    try {
      PresenterManager.getPresenter(null, "123");
      Assert.fail("Exception expected");
    } catch (NullPointerException e) {
    }

    try {
      Activity activity = Mockito.mock(Activity.class);
      Application application = Mockito.mock(Application.class);
      Mockito.when(activity.getApplication()).thenReturn(application);

      PresenterManager.getPresenter(activity, null);
      Assert.fail("Exception expected");
    } catch (NullPointerException e) {
    }
  }

  @Test public void getViewStateThrowsNullPointerException() {
    try {
      PresenterManager.getViewState(null, "123");
      Assert.fail("Exception expected");
    } catch (NullPointerException e) {
    }

    try {
      Activity activity = Mockito.mock(Activity.class);
      Application application = Mockito.mock(Application.class);
      Mockito.when(activity.getApplication()).thenReturn(application);

      PresenterManager.getViewState(activity, null);
      Assert.fail("Exception expected");
    } catch (NullPointerException e) {
    }
  }

  @Test public void putNullPresenterThrowsNullPointerExceptino() {
    MvpPresenter presenter = Mockito.mock(MvpPresenter.class);

    Activity activity = Mockito.mock(Activity.class);
    Application application = Mockito.mock(Application.class);
    Mockito.when(activity.getApplication()).thenReturn(application);

    try {
      PresenterManager.putPresenter(null, "123", presenter);
      Assert.fail("Exception expected");
    } catch (NullPointerException e) {
    }

    try {
      PresenterManager.putPresenter(activity, null, presenter);
      Assert.fail("Exception expected");
    } catch (NullPointerException e) {
    }

    try {
      PresenterManager.putPresenter(activity, "123", null);
      Assert.fail("Exception expected");
    } catch (NullPointerException e) {
    }

  }

  @Test public void putNullViewStateThrowNullPointerException() {
    Object viewState = new Object();

    Activity activity = Mockito.mock(Activity.class);
    Application application = Mockito.mock(Application.class);
    Mockito.when(activity.getApplication()).thenReturn(application);

    try {
      PresenterManager.putViewState(null, "123", viewState);
      Assert.fail("Exception expected");
    } catch (NullPointerException e) {
    }

    try {
      PresenterManager.putViewState(activity, null, viewState);
      Assert.fail("Exception expected");
    } catch (NullPointerException e) {
    }

    try {
      PresenterManager.putViewState(activity, "123", null);
      Assert.fail("Exception expected");
    } catch (NullPointerException e) {
    }
  }


  @Test public void returnsSameScopedCacheAndRegisterLifecycleListener() {

    Activity activity = Mockito.mock(Activity.class);
    Application application = Mockito.mock(Application.class);
    Mockito.when(activity.getApplication()).thenReturn(application);

    ActivityScopedCache scopedCache1 = PresenterManager.getOrCreateActivityScopedCache(activity);
    ActivityScopedCache scopedCache2 = PresenterManager.getOrCreateActivityScopedCache(activity);

    Assert.assertTrue(scopedCache1 == scopedCache2);

    Mockito.verify(application, Mockito.times(1))
        .registerActivityLifecycleCallbacks(PresenterManager.activityLifecycleCallbacks);
  }

  @Test public void removesLifecycleListener() {

    Activity activity = Mockito.mock(Activity.class);
    Application application = Mockito.mock(Application.class);
    Mockito.when(activity.getApplication()).thenReturn(application);
    Mockito.when(activity.isFinishing()).thenReturn(true);

    // This one also registers for
    ActivityScopedCache scopedCache = PresenterManager.getOrCreateActivityScopedCache(activity);

    Mockito.verify(application, Mockito.times(1))
        .registerActivityLifecycleCallbacks(PresenterManager.activityLifecycleCallbacks);

    PresenterManager.activityLifecycleCallbacks.onActivityDestroyed(activity);

    Mockito.verify(application, Mockito.times(1))
        .unregisterActivityLifecycleCallbacks(PresenterManager.activityLifecycleCallbacks);
  }

  @Test public void saveActivityIdAndRestoreFromBundle() {

    Bundle bundle1 = new Bundle();
    Activity portraitActivity1 = Mockito.mock(Activity.class);
    Activity landscapeActivity1 = Mockito.mock(Activity.class);
    MockApplication application = new MockApplication();

    Mockito.when(portraitActivity1.getApplication()).thenReturn(application);
    Mockito.when(landscapeActivity1.getApplication()).thenReturn(application);
    Mockito.when(portraitActivity1.isFinishing()).thenReturn(false);
    Mockito.when(landscapeActivity1.isFinishing()).thenReturn(true);

    // This one also registers for lifecycle events
    ActivityScopedCache scopedCache1 =
        PresenterManager.getOrCreateActivityScopedCache(portraitActivity1);

    Assert.assertTrue(
        application.lifecycleCallbacksList.contains(PresenterManager.activityLifecycleCallbacks));
    Assert.assertEquals(1, application.registerInvocations);
    Assert.assertEquals(0, application.unregisterInvocations);

    PresenterManager.activityLifecycleCallbacks.onActivityPaused(portraitActivity1);
    PresenterManager.activityLifecycleCallbacks.onActivityStopped(portraitActivity1);
    PresenterManager.activityLifecycleCallbacks.onActivitySaveInstanceState(portraitActivity1,
        bundle1);
    PresenterManager.activityLifecycleCallbacks.onActivityDestroyed(portraitActivity1);

    // Don't unregister, because it's a screen orientation change
    Assert.assertTrue(
        application.lifecycleCallbacksList.contains(PresenterManager.activityLifecycleCallbacks));
    Assert.assertEquals(1, application.registerInvocations);
    Assert.assertEquals(0, application.unregisterInvocations);

    String activityId = bundle1.getString(PresenterManager.KEY_ACTIVITY_ID);
    Assert.assertNotNull(activityId);

    //
    // Simulate orientation change
    //

    PresenterManager.activityLifecycleCallbacks.onActivityCreated(landscapeActivity1, bundle1);
    PresenterManager.activityLifecycleCallbacks.onActivityStarted(landscapeActivity1);
    PresenterManager.activityLifecycleCallbacks.onActivityResumed(landscapeActivity1);

    ActivityScopedCache scopedCache2 =
        PresenterManager.getOrCreateActivityScopedCache(landscapeActivity1);
    Assert.assertTrue(scopedCache1 == scopedCache2);

    Assert.assertTrue(
        application.lifecycleCallbacksList.contains(PresenterManager.activityLifecycleCallbacks));
    Assert.assertEquals(1, application.registerInvocations);
    Assert.assertEquals(0, application.unregisterInvocations);

    //
    // Simulate finishing activity permanently
    //
    PresenterManager.activityLifecycleCallbacks.onActivityPaused(landscapeActivity1);
    PresenterManager.activityLifecycleCallbacks.onActivityStopped(landscapeActivity1);
    PresenterManager.activityLifecycleCallbacks.onActivitySaveInstanceState(landscapeActivity1,
        bundle1);
    PresenterManager.activityLifecycleCallbacks.onActivityDestroyed(landscapeActivity1);

    Assert.assertFalse(
        application.lifecycleCallbacksList.contains(PresenterManager.activityLifecycleCallbacks));
    Assert.assertEquals(1, application.registerInvocations);
    Assert.assertEquals(1, application.unregisterInvocations);
  }

  @Test public void saveActivityIdAndRestoreFromBundleWithTwoActivitiesOnBackStack() {

    Bundle bundle1 = new Bundle();
    Bundle bundle2 = new Bundle();
    Activity portraitActivity1 = Mockito.mock(Activity.class);
    Activity landscapeActivity1 = Mockito.mock(Activity.class);
    Activity portraitActivity2 = Mockito.mock(Activity.class);
    Activity landscapeActivity2 = Mockito.mock(Activity.class);
    MockApplication application = new MockApplication();

    Mockito.when(portraitActivity1.getApplication()).thenReturn(application);
    Mockito.when(landscapeActivity1.getApplication()).thenReturn(application);
    Mockito.when(portraitActivity2.getApplication()).thenReturn(application);
    Mockito.when(landscapeActivity2.getApplication()).thenReturn(application);
    Mockito.when(portraitActivity1.isFinishing()).thenReturn(false);
    Mockito.when(landscapeActivity1.isFinishing()).thenReturn(true);
    Mockito.when(portraitActivity2.isFinishing()).thenReturn(false);
    Mockito.when(landscapeActivity2.isFinishing()).thenReturn(true);

    // This one also registers for lifecycle events
    ActivityScopedCache activity1ScopedCache1 =
        PresenterManager.getOrCreateActivityScopedCache(portraitActivity1);

    Assert.assertTrue(
        application.lifecycleCallbacksList.contains(PresenterManager.activityLifecycleCallbacks));
    Assert.assertEquals(1, application.registerInvocations);
    Assert.assertEquals(0, application.unregisterInvocations);

    //
    // start second activity
    //
    PresenterManager.activityLifecycleCallbacks.onActivityCreated(portraitActivity2, null);
    PresenterManager.activityLifecycleCallbacks.onActivityStarted(portraitActivity2);
    PresenterManager.activityLifecycleCallbacks.onActivityResumed(portraitActivity2);

    // This one also registers for lifecycle events
    ActivityScopedCache activity2ScopedCache1 =
        PresenterManager.getOrCreateActivityScopedCache(portraitActivity2);

    Assert.assertTrue(
        application.lifecycleCallbacksList.contains(PresenterManager.activityLifecycleCallbacks));
    Assert.assertEquals(1, application.registerInvocations);
    Assert.assertEquals(0, application.unregisterInvocations);

    //
    // Simulate screen orientation change
    //

    PresenterManager.activityLifecycleCallbacks.onActivityPaused(portraitActivity2);
    PresenterManager.activityLifecycleCallbacks.onActivityStopped(portraitActivity2);
    PresenterManager.activityLifecycleCallbacks.onActivitySaveInstanceState(portraitActivity2,
        bundle2);
    PresenterManager.activityLifecycleCallbacks.onActivityDestroyed(portraitActivity2);
    Assert.assertTrue(
        application.lifecycleCallbacksList.contains(PresenterManager.activityLifecycleCallbacks));
    Assert.assertEquals(1, application.registerInvocations);
    Assert.assertEquals(0, application.unregisterInvocations);
    String activity2Id = bundle2.getString(PresenterManager.KEY_ACTIVITY_ID);
    Assert.assertNotNull(activity2Id);

    PresenterManager.activityLifecycleCallbacks.onActivityPaused(portraitActivity1);
    PresenterManager.activityLifecycleCallbacks.onActivityStopped(portraitActivity1);
    PresenterManager.activityLifecycleCallbacks.onActivitySaveInstanceState(portraitActivity1,
        bundle1);
    PresenterManager.activityLifecycleCallbacks.onActivityDestroyed(portraitActivity1);
    Assert.assertTrue(
        application.lifecycleCallbacksList.contains(PresenterManager.activityLifecycleCallbacks));
    Assert.assertEquals(1, application.registerInvocations);
    Assert.assertEquals(0, application.unregisterInvocations);
    String activity1Id = bundle1.getString(PresenterManager.KEY_ACTIVITY_ID);
    Assert.assertNotNull(activity1Id);

    PresenterManager.activityLifecycleCallbacks.onActivityCreated(landscapeActivity2, bundle2);
    PresenterManager.activityLifecycleCallbacks.onActivityStarted(landscapeActivity2);
    PresenterManager.activityLifecycleCallbacks.onActivityResumed(landscapeActivity2);

    ActivityScopedCache activity2ScopedCache2 =
        PresenterManager.getOrCreateActivityScopedCache(landscapeActivity2);
    Assert.assertTrue(activity2ScopedCache1 == activity2ScopedCache2);

    PresenterManager.activityLifecycleCallbacks.onActivityCreated(landscapeActivity1, bundle1);
    PresenterManager.activityLifecycleCallbacks.onActivityStarted(landscapeActivity1);
    PresenterManager.activityLifecycleCallbacks.onActivityResumed(landscapeActivity1);

    ActivityScopedCache activity1ScopedCache2 =
        PresenterManager.getOrCreateActivityScopedCache(landscapeActivity1);
    Assert.assertTrue(activity1ScopedCache1 == activity1ScopedCache2);

    Assert.assertTrue(
        application.lifecycleCallbacksList.contains(PresenterManager.activityLifecycleCallbacks));
    Assert.assertEquals(1, application.registerInvocations);
    Assert.assertEquals(0, application.unregisterInvocations);

    //
    // Simulate finishing activity2 permanently
    //
    PresenterManager.activityLifecycleCallbacks.onActivityPaused(landscapeActivity2);
    PresenterManager.activityLifecycleCallbacks.onActivityStopped(landscapeActivity2);
    PresenterManager.activityLifecycleCallbacks.onActivitySaveInstanceState(landscapeActivity2,
        bundle2);
    PresenterManager.activityLifecycleCallbacks.onActivityDestroyed(landscapeActivity2);
    Assert.assertTrue(
        application.lifecycleCallbacksList.contains(PresenterManager.activityLifecycleCallbacks));
    Assert.assertEquals(1, application.registerInvocations);
    Assert.assertEquals(0, application.unregisterInvocations);

    //
    // Simulate finishing activity1 permanently
    //
    PresenterManager.activityLifecycleCallbacks.onActivityPaused(landscapeActivity1);
    PresenterManager.activityLifecycleCallbacks.onActivityStopped(landscapeActivity1);
    PresenterManager.activityLifecycleCallbacks.onActivitySaveInstanceState(landscapeActivity1,
        bundle1);
    PresenterManager.activityLifecycleCallbacks.onActivityDestroyed(landscapeActivity1);
    Assert.assertFalse(
        application.lifecycleCallbacksList.contains(PresenterManager.activityLifecycleCallbacks));
    Assert.assertEquals(1, application.registerInvocations);
    Assert.assertEquals(1, application.unregisterInvocations);
  }

  @Test public void getActivityScopeReturnsNullIfNotExisting() {
    Activity activity = Mockito.mock(Activity.class);
    Assert.assertNull(PresenterManager.getActivityScope(activity));
  }

  @Test public void getActivityScopeReturnsExistingOne() {
    Activity activity = Mockito.mock(Activity.class);
    Application application = Mockito.mock(Application.class);

    Mockito.when(activity.getApplication()).thenReturn(application);

    ActivityScopedCache scope1 =
        PresenterManager.getOrCreateActivityScopedCache(activity);
    Assert.assertNotNull(scope1);
    Assert.assertEquals(scope1, PresenterManager.getActivityScope(activity));
  }

  @Test
  public void getPresenterReturnsNull(){
    Activity activity = Mockito.mock(Activity.class);
    Application application = Mockito.mock(Application.class);
    Mockito.when(activity.getApplication()).thenReturn(application);

    Assert.assertNull(PresenterManager.getPresenter(activity, "viewId123"));
  }

  @Test
  public void getViewStateReturnsNull(){
    Activity activity = Mockito.mock(Activity.class);
    Application application = Mockito.mock(Application.class);
    Mockito.when(activity.getApplication()).thenReturn(application);

    Assert.assertNull(PresenterManager.getViewState(activity, "viewId123"));
  }

  @Test
  public void putGetRemovePresenter(){
    Activity activity = Mockito.mock(Activity.class);
    Application application = Mockito.mock(Application.class);
    Mockito.when(activity.getApplication()).thenReturn(application);

    MvpPresenter<MvpView> presenter = new MvpPresenter<MvpView>() {
      @Override public void attachView(MvpView view) {
      }

      @Override public void detachView(boolean retainInstance) {
      }

      @Override public void detachView() {
      }

      @Override public void destroy() {
      }
    };

    String viewId ="123";
    Assert.assertNull(PresenterManager.getPresenter(activity, viewId));

    PresenterManager.putPresenter(activity, viewId, presenter);
    Assert.assertTrue(presenter == PresenterManager.getPresenter(activity, viewId));

    PresenterManager.remove(activity, viewId);
    Assert.assertNull(PresenterManager.getPresenter(activity, viewId));
  }


  @Test
  public void putGetRemoveViewState(){
    Activity activity = Mockito.mock(Activity.class);
    Application application = Mockito.mock(Application.class);
    Mockito.when(activity.getApplication()).thenReturn(application);

    Object viewState = new Object();

    String viewId ="123";
    Assert.assertNull(PresenterManager.getViewState(activity, viewId));

    PresenterManager.putViewState(activity, viewId, viewState);
    Assert.assertTrue(viewState == PresenterManager.getViewState(activity, viewId));

    PresenterManager.remove(activity, viewId);
    Assert.assertNull(PresenterManager.getPresenter(activity, viewId));
  }
}
