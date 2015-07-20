/*
 *  Copyright 2015. Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby.mvp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.hannesdorfmann.mosby.mvp.Defaults.defaultValue;
import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * Dynamically proxy to generate a new object instance for a given class by using reflections
 *
 * @author Jens Dirller
 * @since 1.2.0
 */
final class NoOp {

  private static final InvocationHandler DEFAULT_VALUE = new DefaultValueInvocationHandler();

  private NoOp() {
    // no instances
  }

  @SuppressWarnings("unchecked") public static <T> T of(Class<T> interfaceClass) {
    return (T) newProxyInstance(interfaceClass.getClassLoader(), new Class[] { interfaceClass },
        DEFAULT_VALUE);
  }

  private static class DefaultValueInvocationHandler implements InvocationHandler {

    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return defaultValue(method.getReturnType());
    }
  }
}