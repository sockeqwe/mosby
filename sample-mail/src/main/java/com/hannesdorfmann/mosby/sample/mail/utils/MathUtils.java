package com.hannesdorfmann.mosby.sample.mail.utils;

import java.util.Random;

/**
 * Little helper class that provides some useful mathematical methods
 * @author Hannes Dorfmann
 */
public class MathUtils {

  private static Random random = new Random();

  /**
   * This method maps a number x, which is in the range [sourceStart,
   * sourceEnd], to a new range [targetStart, targetEnd]
   *
   * <p>
   * sourceStart <= x <= sourceEnd <br/>
   * targetStart <= returnValue <= targetEnd
   * </p>
   *
   * @param x
   *            The value that should be mapped
   * @param sourceStart
   *            The source range start (inclusive)
   * @param sourceEnd
   *            The source range end (inclusive)
   * @param targetStart
   *            The target range start (inclusive)
   * @param targetEnd
   *            The target range end (inclusive)
   * @return The corresponding value of x in the target range
   */
  public static float mapPoint(float x, float sourceStart, float sourceEnd,
      float targetStart, float targetEnd) {

    if (x <= sourceStart) {
      return targetStart;
    }

    if (x >= sourceEnd) {
      return targetEnd;
    }

    return (x - sourceStart) / (sourceEnd - sourceStart)
        * (targetEnd - targetStart) + targetStart;
  }

  /**
   * <b>This is the same as
   * {@link #mapPoint(float, float, float, float, float)}but without rounding
   * the integer up. Use {@link #mapPointRound(float, float, float, int, int)}
   * if you want rounded results</b>
   * <p>
   * This method maps a number x, which is in the range [sourceStart,
   * sourceEnd], to a new range [targetStart, targetEnd]
   * </p>
   * <p>
   * sourceStart <= x <= sourceEnd <br/>
   * targetStart <= returnValue <= targetEnd
   * </p>
   *
   * @param x
   *            The value that should be mapped
   * @param sourceStart
   *            The source range start (inclusive)
   * @param sourceEnd
   *            The source range end (inclusive)
   * @param targetStart
   *            The target range start (inclusive)
   * @param targetEnd
   *            The target range end (inclusive)
   * @return The corresponding value of x in the target range
   */
  public static int mapPoint(float x, float sourceStart, float sourceEnd,
      int targetStart, int targetEnd) {

    if (x <= sourceStart) {
      return targetStart;
    }

    if (x >= sourceEnd) {
      return targetEnd;
    }

    float fRes = (x - sourceStart) / (sourceEnd - sourceStart)
        * (targetEnd - targetStart) + targetStart;

    return (int) fRes;
  }

  /**
   * <b>This is the same as
   * {@link #mapPoint(float, float, float, float, float)}but rounds to
   * integer.</b>
   * <p>
   * This method maps a number x, which is in the range [sourceStart,
   * sourceEnd], to a new range [targetStart, targetEnd]
   * </p>
   * <p>
   * sourceStart <= x <= sourceEnd <br/>
   * targetStart <= returnValue <= targetEnd
   * </p>
   *
   * @param x
   *            The value that should be mapped
   * @param sourceStart
   *            The source range start (inclusive)
   * @param sourceEnd
   *            The source range end (inclusive)
   * @param targetStart
   *            The target range start (inclusive)
   * @param targetEnd
   *            The target range end (inclusive)
   * @return The corresponding value of x in the target range
   */
  public static int mapPointRound(float x, float sourceStart,
      float sourceEnd, int targetStart, int targetEnd) {

    if (x <= sourceStart) {
      return targetStart;
    }

    if (x >= sourceEnd) {
      return targetEnd;
    }

    float fRes = (x - sourceStart) / (sourceEnd - sourceStart)
        * (targetEnd - targetStart) + targetStart;

    return (int) (fRes + 0.5f);
  }

  /**
   * Checks if a value is between up and down (inclusive up and down)
   *
   * @param x the value to check
   * @param down the lower bound
   * @param up the upper bound
   * @return true, if between bounds, otherwise false
   */
  public static boolean isBetween(float x, float down, float up) {
    return x >= down && x <= up;
  }

  /**
   * Get a random int
   *
   * @return a random integer
   */
  public static int randomInt() {
    return random.nextInt(Integer.MAX_VALUE - 1);
  }

}