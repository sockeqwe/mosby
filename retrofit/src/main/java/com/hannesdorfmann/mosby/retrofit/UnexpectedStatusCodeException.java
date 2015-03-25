/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby.retrofit;

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
