package com.hannesdorfmann.mosby.retrofit.exception;

/**
 * A simple exception that is thrown if an unexpected http status code has returned in server
 * response
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class UnexpectedStatusCodeException extends Exception {

  private int statusCode;

  public UnexpectedStatusCodeException(int statusCode) {
    super("An unexcpected http status code in http response: " + statusCode);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
