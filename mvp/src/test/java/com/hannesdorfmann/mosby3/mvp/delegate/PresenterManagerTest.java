package com.hannesdorfmann.mosby3.mvp.delegate;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class PresenterManagerTest {

  @Test public void getPut() {
    PresenterManager.OrientationChangeFragment fragment =
        new PresenterManager.OrientationChangeFragment();

    PresenterManager.CacheEntry entry1 = new PresenterManager.CacheEntry(null);
    PresenterManager.CacheEntry entry2 = new PresenterManager.CacheEntry(null);

    fragment.put(1, entry1);
    fragment.put(2, entry2);

    Assert.assertTrue(fragment.get(1) == entry1);
    Assert.assertTrue(fragment.get(2) == entry2);
  }
}
