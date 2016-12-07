package com.hannesdorfmann.mosby3.mvi;

/**
 * This class is used to access the current ViewState of a certain view.
 * Calling get() will always return the latest ViewState
 *
 * @author Hannes Dorfmann
 */
abstract class ViewState<VS> {

  /**
   * Package visibility only
   */
  ViewState() {
  }

  /**
   * Get the current ViewState
   */
  abstract VS get();
}
