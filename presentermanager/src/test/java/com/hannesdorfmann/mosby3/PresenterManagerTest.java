package com.hannesdorfmann.mosby3;

import com.hannesdorfmann.mosby3.PresenterManager;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class PresenterManagerTest {

  // TODO more tests!

  @Test public void getPut() {
    PresenterManager.PresenterManagerFragment fragment =
        new PresenterManager.PresenterManagerFragment();

    PresenterManager.CacheEntry entry1 = new PresenterManager.CacheEntry(null);
    PresenterManager.CacheEntry entry2 = new PresenterManager.CacheEntry(null);

    fragment.put("1", entry1);
    fragment.put("2", entry2);

    Assert.assertTrue(fragment.get("1") == entry1);
    Assert.assertTrue(fragment.get("2") == entry2);
  }
}
