package com.hannesdorfmann.mosby.mvp.delegate;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class OrientationChangeFragmentTest {

  @Test public void getPut() {
    OrientationChangeManager.OrientationChangeFragment fragment =
        new OrientationChangeManager.OrientationChangeFragment();

    OrientationChangeManager.CacheEntry entry1 = new OrientationChangeManager.CacheEntry(null);
    OrientationChangeManager.CacheEntry entry2 = new OrientationChangeManager.CacheEntry(null);

    fragment.put(1, entry1);
    fragment.put(2, entry2);

    Assert.assertTrue(fragment.get(1) == entry1);
    Assert.assertTrue(fragment.get(2) == entry2);
  }
}
