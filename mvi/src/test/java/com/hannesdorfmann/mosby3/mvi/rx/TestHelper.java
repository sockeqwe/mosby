package com.hannesdorfmann.mosby3.mvi.rx;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.internal.util.ExceptionHelper;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.mockito.Mockito;

/**
 * @author Hannes Dorfmann
 */

public class TestHelper {

  /**
   * Synchronizes the execution of two runnables (as much as possible)
   * to test race conditions.
   * <p>The method blocks until both have run to completion.
   *
   * @param r1 the first runnable
   * @param r2 the second runnable
   * @param s the scheduler to use
   */
  public static void race(final Runnable r1, final Runnable r2, Scheduler s) {
    final AtomicInteger count = new AtomicInteger(2);
    final CountDownLatch cdl = new CountDownLatch(2);

    final Throwable[] errors = { null, null };

    s.scheduleDirect(new Runnable() {
      @Override public void run() {
        if (count.decrementAndGet() != 0) {
          while (count.get() != 0) {
          }
        }

        try {
          try {
            r1.run();
          } catch (Throwable ex) {
            errors[0] = ex;
          }
        } finally {
          cdl.countDown();
        }
      }
    });

    if (count.decrementAndGet() != 0) {
      while (count.get() != 0) {
      }
    }

    try {
      try {
        r2.run();
      } catch (Throwable ex) {
        errors[1] = ex;
      }
    } finally {
      cdl.countDown();
    }

    try {
      if (!cdl.await(5, TimeUnit.SECONDS)) {
        throw new AssertionError("The wait timed out!");
      }
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex);
    }
    if (errors[0] != null && errors[1] == null) {
      throw ExceptionHelper.wrapOrThrow(errors[0]);
    }

    if (errors[0] == null && errors[1] != null) {
      throw ExceptionHelper.wrapOrThrow(errors[1]);
    }

    if (errors[0] != null && errors[1] != null) {
      throw new CompositeException(errors);
    }
  }

  public static <T> Observer<T> mockObserver() {
    return Mockito.mock(Observer.class);
  }
}
