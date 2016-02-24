package com.hannesdorfmann.mosby.mvp.delegate;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class OrientationChangeManagerTest {


  @Test
  public void nextViewId(){
    OrientationChangeManager manager = new OrientationChangeManager();
    Assert.assertEquals(1, manager.nextViewId());
    Assert.assertEquals(2, manager.nextViewId());

    OrientationChangeManager manager1 = new OrientationChangeManager();
    Assert.assertEquals(3, manager1.nextViewId());

  }
}
